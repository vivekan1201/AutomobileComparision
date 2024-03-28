package com.example.carcomparision;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service

public class ToyotaCarScraper {
    private CarBST carBST = new CarBST();
    @Autowired
    private CarService carService;
    @Autowired
    private ObjectMapper objectMapper;
    private static List<Car> cachedCars;
    private Map<String, Integer> searchFrequencyMap = new HashMap<>();
    private Map<String, Integer> frequencyCountMap = new HashMap<>();
    // Set up Chrome WebDriver
    public List<Car> getToyotaCars (List<Car> cars) throws InterruptedException, URISyntaxException, IOException {
        // URLs to scrape
        String[] urls = {
                "https://www.toyota.ca/toyota/en/vehicles/gr86/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/gr-corolla/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/supra/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/prius/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/prius-prime/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/corolla-hatchback/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/corolla/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/camry/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/crown/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/corolla-cross/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/rav4/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/rav4-prime/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/venza/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/highlander/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/grand-highlander/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/4runner/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/sequoia/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/sequoia/models-specifications",
                "https://www.toyota.ca/toyota/en/vehicles/tundra/models-specifications"

        };

        // Set up Chrome WebDriver
        String chromeDriverPath = "C:\\Users\\shiva\\Downloads\\chromedriver-win64\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        // Initialize ChromeDriver options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode


        // Iterate through each URL and scrape car information
        WebDriver driver = new ChromeDriver(options);
        driver.get("https://www.toyota.ca/toyota/en/vehicles/crossovers-suvs");
        List<WebElement> elements = driver.findElements(By.cssSelector("[class='heading-1 bolder uppercase white']"));
        List<String> suvList = new ArrayList<>();

        // Iterate over the elements
        for (WebElement element : elements) {
            // Get the text of the element
            String text = element.getText();

            // Split the text based on the "&" character
            String[] parts = text.split("&");

            // Get the first part and trim any leading or trailing spaces
            String firstPart = parts[0].trim();

            // Add the first part to the list
            suvList.add(firstPart);
        }

        driver.get("https://www.toyota.ca/toyota/en/vehicles/pickup-trucks");
        elements = driver.findElements(By.cssSelector("[class=\"heading-1\"] span"));
        List<String> truckList = new ArrayList<>();

        // Iterate over the elements
        for (WebElement element : elements) {
            // Get the text of the element
            String text = element.getText();

            // Split the text based on the "&" character
            String[] parts = text.split("&");

            // Get the first part and trim any leading or trailing spaces
            String firstPart = parts[0].trim();

            // Add the first part to the list
            truckList.add(firstPart);
        }

        boolean isFirstIteration = true;
        for (String url : urls) {
            driver.manage().window().maximize();
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            // Extract car information

            List<WebElement> nameElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[class=\"heading-4 light\"]")));
            String[] part = nameElement.get(0).getText().split("&");
            String carModel1 = part[0].trim(); // Trim to remove extra spaces

            // Split the car model to get the year and car name
            String[] carModelParts = carModel1.split("\\s+", 2); // Split by first space
            carModel1 = carModelParts[0];
            String carName = carModelParts[1];
            int carModel= Integer.parseInt(carModel1);

            // Print the extracted year and car name
            //("Year: " + carModel);
            //("Car Name: " + carName);
            // Find the index of 'vehicles' in the URL
            Pattern pattern = Pattern.compile("www\\.([a-zA-Z-]+)\\.");
            Matcher matcher = pattern.matcher(url);

            // Find the brand name using the regular expression
            String carCompany = null;
            if (matcher.find()) {
                String brand = matcher.group(1);
                carCompany = brand.substring(0, 1).toUpperCase() + brand.substring(1);; // Output: "nissan"
            }
            //(carCompany);
            scrollPageSlowly(driver);
            List<WebElement> engine = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[id=\"powertrain-&-mechanical-engine\"]~tr ~tr [class=\"ng-binding\"]")));
            String transmission = engine.get(0).getText();
            //(transmission);

//            List<WebElement> seat = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[id=\"dimensions-dimensions\"]~tr~tr td")));
//            String seatCapacity = seat.get(21).getText();
//            //(seatCapacity);
            // Locate the parent element containing all the rows
            List<WebElement> tdElements = driver.findElements(By.cssSelector("[id='dimensions-dimensions']~tr~tr td"));
            String seatCapacity = null;
            // Iterate through each <td> element
            for (int i = 0; i < tdElements.size(); i++) {
                WebElement tdElement = tdElements.get(i);

                // Check if the text of the <td> element is "Seating Capacity"
                if (tdElement.getText().contains("Seating Capacity")) {
                    // Get the next <td> element
                    WebElement nextTdElement = tdElements.get(i + 1);

                    // Print the text of the next <td> element
//                    //(nextTdElement.getText());
                    seatCapacity=nextTdElement.getText();

                    // Break out of the loop since we found the desired element
                    break;
                }
            }
            //(seatCapacity);
            String fuelEfficiency=null;
            for (int i = 0; i < tdElements.size(); i++) {
                WebElement tdElement = tdElements.get(i);

                // Check if the text of the <td> element is "Fuel Consumption - City/Highway/Combined L/100km"
                if (tdElement.getText().equals("Fuel Consumption - City/Highway/Combined L/100km")) {
                    // Get the next <td> element
                    WebElement nextTdElement = tdElements.get(i + 1);

                    // Print the text of the next <td> element
//                    //(nextTdElement.getText());
                    fuelEfficiency=nextTdElement.getText();
                    // Break out of the loop since we found the desired element
                    break;
                }
            }
//            //(fuelEfficiency);


            String[] fr = fuelEfficiency.split("/");

//             Trim each part and get the first one
            fuelEfficiency = fr[0].trim();
            //(fuelEfficiency);

            WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[class='grid-container content-container']")));

            // Find all the links inside the container
            List<WebElement> links = container.findElements(By.tagName("a"));

            // Click on the first link
            if (!links.isEmpty()) {
                links.get(0).click();
            }
            if(isFirstIteration) {
                WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("continue-button")));
                continueButton.click();
                isFirstIteration=false;
            }
            List<WebElement> priceElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[class=\"heading-5 ng-binding\"]")));
            String price1 = priceElement.get(0).getText().trim();
            // Clean and parse price
            String cleanString = price1.replaceAll("[$,]", "");
            int price = (int) Double.parseDouble(cleanString);
            //(price);

            List<WebElement> img = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[class=\"model-image-container\"] img")));
            String imageLink = img.get(0).getAttribute("src");
            //(imageLink);

            String suv;
            if (suvList.contains(carName)) {
                suv="SUV";
            }
            else if(truckList.contains(carName)){
                suv="Truck";
            }
            else if(carName.contains("Prius") ||carName.toLowerCase().contains("hatchback")||carName.toLowerCase().contains("gr corolla")){
                suv="Hatchback";
            }
            else {
                suv="Sedan";
            }
            //(suv);
            Car car = new Car(carModel, carName, carCompany, transmission, price, imageLink, suv, seatCapacity, fuelEfficiency);

            cars.add(car);
            car.printData();
        }

        // Do whatever you want with the list of cars (e.g., store in database)
        driver.quit();
        return cars;
    }
    private static void scrollPageSlowly(WebDriver driver) {
        // Execute JavaScript to scroll the page slowly
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long windowHeight = (long) js.executeScript("return window.innerHeight");
        long documentHeight = (long) js.executeScript("return document.body.scrollHeight");
        long scrollDistance = documentHeight / 100; // Adjust this value to change scroll speed
        long currentPosition = 0;
        while (currentPosition < documentHeight) {
            js.executeScript("window.scrollBy(0, " + scrollDistance + ")");
            currentPosition += scrollDistance;
            try {
                Thread.sleep(100); // Adjust this value to change scroll speed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

