package com.example.carcomparision;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class MitsubishiCarScraping {

    private CarBST carBST = new CarBST();
    @Autowired
    private CarService carService;
    @Autowired
    private ObjectMapper objectMapper;
    private static List<Car> cachedCars;
    private Map<String, Integer> searchFrequencyMap = new HashMap<>();
    private Map<String, Integer> frequencyCountMap = new HashMap<>();

    // Set up Chrome WebDriver
    public  List<Car> getMitsubishiCars (List<Car> cars) throws InterruptedException, URISyntaxException, MalformedURLException {
        // URLs to scrape
        String[] urls = {
                "https://www.mitsubishi-motors.ca/en/vehicles/mirage/specs",
                "https://www.mitsubishi-motors.ca/en/vehicles/outlander-phev/specs",
                "https://www.mitsubishi-motors.ca/en/vehicles/outlander/specs",
                "https://www.mitsubishi-motors.ca/en/vehicles/eclipse-cross/specs",
                "https://www.mitsubishi-motors.ca/en/vehicles/rvr/specs"


        };

        // Set up Chrome WebDriver
        String chromeDriverPath = "C:\\Users\\shiva\\Downloads\\chromedriver-win64\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        // Initialize ChromeDriver options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headed"); // Run in headless mode
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Iterate through each URL and scrape car information
        for (String url : urls) {

            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[class='specifications__header___qJTtl']")));

            // Extract the text content from the element
            String input = elements.get(0).getText();
            input = input.replace(" SPECS", "");

            // Split the input string by whitespace
            String[] parts = input.split("\\s+");

            // Initialize variables for car year, model, and name
            String year = parts[0];
            String model = year;
            String name = "";

            // Concatenate the remaining parts to get the car name
            for (int i = 1; i < parts.length; i++) {
                if (!parts[i].equals(year)) {
                    if (!name.isEmpty()) {
                        name += " ";
                    }
                    name += parts[i];
                }
            }

            // Print the results
            int carModel = Integer.parseInt(year);
            String carName = name;
            Pattern pattern = Pattern.compile("www\\.([a-zA-Z-]+)-");

            // Create a matcher for the given URL
            Matcher matcher = pattern.matcher(url);

            // Find the brand name using the regular expression
            String carCompany = null;
            if (matcher.find()) {
                carCompany = matcher.group(1);
                carCompany=carCompany.substring(0, 1).toUpperCase() + carCompany.substring(1);
            }

            List<WebElement> engine = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[id=\"navbar_item_Engine\"]~[class=\"spec-comparison-table__labelWrapper___ENu0v\"] [class=\"spec-comparison-table__value___3FkKb\"]")));
            String transmission = engine.get(0).getText();

            List<WebElement> priceElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[class=\"specifications__dropPrice___2ikp9\"] [class=\"notranslate\"]")));
            String price1 = priceElement.get(0).getText().trim();
            // Clean and parse price
            String cleanString = price1.replaceAll("[$,]", "");
            int price = Integer.parseInt(cleanString);

            List<WebElement> img = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[class=\"specifications__greyContainer___paVQp \"] img")));
            String imageLink = img.get(0).getAttribute("src");

            WebElement element = driver.findElement(By.cssSelector("[class='specifications__greyContainer___paVQp '] img"));

            // Get the value of the 'alt' attribute of the image
            String altText = element.getAttribute("alt");

            // Check if the alt text contains the keywords
            String suv="";
            if (altText.contains("SUV") || altText.contains("PHEV") || altText.contains("Cross")) {
                suv="SUV";
            } else {
                suv="Hatchback";
            }
            List<WebElement> seat = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[id=\"navbar_item_InteriorDimensions\"] ~ div span ~ div div")));
            String seatCapacity = seat.get(0).getText();
            if (!Objects.equals(seatCapacity, "5")) {
                seat = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[id=\"navbar_item_WeightsCapacities\"]~div span ~div div")));
                seatCapacity=seat.get(3).getText();
            }
            if(Objects.equals(seatCapacity, "680 / 1500")){
                seatCapacity="5";
            }

            List<WebElement> elements1 = driver.findElements(By.cssSelector("[id='navbar_item_EstimatedFuelConsumption']~div span~div div"));

            // Extract text from the first element (if present)
            String fuelEfficiency = "";
            if (!elements1.isEmpty()) {
                fuelEfficiency = elements1.get(0).getText();
            } else {
                // If the first locator is not found, try the second locator
                List<WebElement> elements2 = driver.findElements(By.cssSelector("[id='navbar_item_FuelEconomy']~div div div"));
                if (!elements2.isEmpty()) {
                    fuelEfficiency = elements2.get(0).getText();
                }
            }
            String[] fr = fuelEfficiency.split("/");

            // Trim each part and get the first one
            String fu = fr[0].trim();
            fuelEfficiency=fu;
            // Convert the first number string to a double
            Car car = new Car(carModel, carName, carCompany, transmission, price, imageLink, suv, seatCapacity, fuelEfficiency);
            cars.add(car);
            car.printData();

            // Quit the WebDriver
            // Do whatever you want with the list of cars (e.g., store in database)
        }
        driver.quit();
        return cars;
    }
}
