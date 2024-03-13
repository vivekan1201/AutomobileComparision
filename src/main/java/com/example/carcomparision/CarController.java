package com.example.carcomparision;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cars")
public class CarController {


    private CarBST carBST = new CarBST();

    @Autowired
   private CarService carService;
    @Autowired
    private ObjectMapper objectMapper;

    private List<Car> cachedCars;

    private Map<String, Integer> searchFrequencyMap = new HashMap<>();
    private Map<String, Integer> frequencyCountMap = new HashMap<>();

    @GetMapping
    public List<Car> getCars() {
        // Set up Chrome WebDriver
        String chromeDriverPath = "C:\\Users\\shiva\\Downloads\\chromedriver-win64\\chromedriver.exe";
        System.out.println("ChromeDriver Path: " + chromeDriverPath);
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        // Initialize ChromeDriver

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode
        WebDriver driver = new ChromeDriver(options);
        // Navigate to the website with dynamic content
        driver.get("https://www.mazda.ca/en/shopping/build-and-price/?engine_name=google&keyword_id=p60478844754&campaign_id=71700000079743417&gad_source=1&gclid=Cj0KCQiArrCvBhCNARIsAOkAGcUJYqreMVHx9YM9MJUCmXUDkFPUq3FxXtCb_5E3QUAQaO36ddgsGa8aAlUpEALw_wcB&gclsrc=aw.ds");

        // Wait for dynamic content to load (you may need to adjust this)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Find car elements
        List<WebElement> carElements = driver.findElements(By.className("mz-tabs__list"));

        List<Car> cars = new ArrayList<>();

        // Iterate through each car element and extract details
        for (WebElement carElement : carElements) {
            try {
                // Extract car name
                WebElement nameElement = carElement.findElement(By.tagName("h5"));
                String carName = nameElement.getText().trim();

                if (!carName.isEmpty()) {
                    // Extract car description (price and type)
                    WebElement descriptionElement = carElement.findElement(By.xpath(".//div[contains(@class, 'mz-jelly-content')]"));
                    String carDescription = descriptionElement.getText().trim();

                    // Extract car image link
                    WebElement imageElement = carElement.findElement(By.tagName("img"));
                    String imageLink = imageElement.getAttribute("src");

                    // Extract price from description
                    int price = Integer.parseInt(extractPrice(carDescription));
                    String carType= extractCarType(carDescription);

                    // Create Car object and add to list
                    Car car = new Car(carName,carDescription,price, imageLink,carType);
                    cars.add(car);
                }
            } catch (Exception e) {
                // Handle any exceptions during scraping
                e.printStackTrace();
            }
        }

        // Quit the driver
        driver.quit();

       // storeCarsInCouchDB(cars);
        //carService.addCarsToDocument(cars);
        if (cachedCars == null) {
            // If not cached, retrieve cars from the database
            cachedCars = cars;
        }

        return cars;
    }

//    private void storeCarsInCouchDB(List<Car> cars) {
//        for (Car car : cars) {
//            couchbaseTemplate.save(car); // Assuming Car is a CouchbaseDocument
//        }
//    }

    private String extractPrice(String carDescription) {
        String[] parts = carDescription.split(" - ");
        if (parts.length > 1) {
            String pricePart = parts[1];
            String[] priceSplit = pricePart.split("Starting at \\$");
            if (priceSplit.length > 1) {
                // Remove commas from the price string
                String priceString = priceSplit[1].split(" ")[0].replace(",", "");
                return priceString;
            }
        }
        return ""; // Return an empty string if the price cannot be extracted
    }

    public static String extractCarType(String description) {
        // Common car types
        List<String> carTypes = Arrays.asList("hatchback", "suv", "sedan", "convertible");


        // Convert description to lowercase for case-insensitive search
        String lowercaseDescription = description.toLowerCase();

        // Search for common car types in the description
        for (String carType : carTypes) {
            if (lowercaseDescription.contains(carType)) {
                return carType;
            }
        }

        // Return "Unknown" if no common car type is found
        return "Unknown";
    }

    @GetMapping("/document/{id}")
    public ResponseEntity<Object> getDocumentById(@PathVariable String id) {
        // Call the method to retrieve the document by ID
        Document document=carService.getDocumentById(id);
        if (document != null) {
            try {
                String jsonDocument = objectMapper.writeValueAsString(document);
                return ResponseEntity.ok(jsonDocument); // Return JSON string in response entity
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error converting document to JSON");
            }
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if document not found
        }
    }

    @GetMapping("/filterCars")
    public List<Car> populateAndFilterCars(
            @RequestParam(value = "carType", required = false) String carType,
            @RequestParam(value = "minPrice", required = false) int minPrice,
            @RequestParam(value = "maxPrice", required = false) int maxPrice) {

        // Retrieve cars from the database
        List<Car> cars = cachedCars;

        // Populate the CarBST with the retrieved cars
        for (Car car : cars) {
            carBST.insert(car);
        }

        // Filter cars based on the provided parameters
        return carBST.filterCars(carType, minPrice, maxPrice);
    }

    @GetMapping("/search")
    public List<Car> searchCars(@RequestParam String keyword) {

        incrementSearchFrequency(keyword);
        // Retrieve relevant cars based on the search keyword
        List<Car> relevantCars = carService.searchCars(keyword,cachedCars);
        // Increment frequency count for each searched term
        return relevantCars;
    }

    private void incrementSearchFrequency(String keyword) {
        searchFrequencyMap.put(keyword, searchFrequencyMap.getOrDefault(keyword, 0) + 1);
    }

    @GetMapping("/frequencyCount")
    public ResponseEntity<Integer> getFrequencyCount(@RequestParam String word, @RequestParam String url) {
        int count = carService.getFrequencyCount(word, url);
        if (count >= 0) {
            return ResponseEntity.ok(count);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1); // Error retrieving frequency count
        }
    }

    }



