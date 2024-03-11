package com.example.carcomparision;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

//    @Autowired
//    private CouchbaseTemplate couchbaseTemplate;

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
                    String price = extractPrice(carDescription);
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
                return priceSplit[1].split(" ")[0];
            }
        }
        return "";
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


}
