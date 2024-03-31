package com.example.carcomparision;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
        import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class NissanWebCrawling {
    private CarBST carBST = new CarBST();
    @Autowired
    private CarService carService;
    @Autowired
    private ObjectMapper objectMapper;
    private static List<Car> cachedCars;
    private Map<String, Integer> searchFrequencyMap = new HashMap<>();
    private Map<String, Integer> frequencyCountMap = new HashMap<>();
    // Set up Chrome WebDriver

    // URLs to scrape
    static String[] urls = {
            "https://www.nissan.ca/vehicles/crossovers-suvs/murano/specs.html#modelName=SV|AWD%20CVT",
            "https://www.nissan.ca/vehicles/crossovers-suvs/2024-kicks/specs-trims.html#modelName=S|CVT",
            "https://www.nissan.ca/vehicles/crossovers-suvs/pathfinder/specs.html#modelName=S|4WD",
            "https://www.nissan.ca/vehicles/crossovers-suvs/rogue/specs.html#modelName=S|AWD%20CVT",
            "https://www.nissan.ca/vehicles/crossovers-suvs/armada/specs.html#modelName=SL|AT%204x4",
            "https://www.nissan.ca/vehicles/cars/versa/compare-specs.html#modelName=S|FWD%20CVT",
            "https://www.nissan.ca/vehicles/cars/sentra/specs.html#modelName=S%20MT|6MT",
            "https://www.nissan.ca/vehicles/cars/2024-altima/specs/compare-specs.html#modelName=S|AWD",
            "https://www.nissan.ca/vehicles/electric-cars/leaf/specs.html#modelName=SV",
            "https://www.nissan.ca/vehicles/electric-cars/ariya/specs.html#modelName=ENGAGE|FWD",
            "https://www.nissan.ca/vehicles/sports-cars/gt-r/specs.html#modelName=Premium|AWD",
            "https://www.nissan.ca/vehicles/sports-cars/z/specs.html#modelName=Sport%206MT|6MT",
            "https://www.nissan.ca/vehicles/trucks/frontier/specs.html#modelName=Crew%20Cab%20PRO-4X|4x4"


    };

public List<Car> getNissanCars(){
        // Set up Chrome WebDriver
        String chromeDriverPath = "C:\\Users\\visha\\Desktop\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        // Initialize ChromeDriver options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headed"); // Run in headless mode

        List<Car> cars = new ArrayList<>();

        // Iterate through each URL and scrape car information
        WebDriver driver = new ChromeDriver(options);
        try {
            for (String url : urls) {
//            driver.manage().window().maximize();

                if(dataValidation.isValidURL(url)) {
                    driver.get(url);
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
                    // Extract car information

                    List<WebElement> nameElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[class='c_283_vehicle_name--desktop']")));
                    String modelName = nameElement.get(0).getText().replaceAll("®", "").trim();
                    String regex = "(?<=2024|2023)";
                    String[] carParts = modelName.split(regex);
                    String carName = carParts[1].trim();
                    if (carName.contains("Nissan")) {
                        carName = carName.replace("Nissan ", "");
                    }
                    // Find the index of 'vehicles' in the URL
                    Pattern pattern = Pattern.compile("www\\.([a-zA-Z-]+)\\.");
                    Matcher matcher = pattern.matcher(url);

                    // Find the brand name using the regular expression
                    String carCompany = null;
                    if (matcher.find()) {
                        String brand = matcher.group(1);
                        carCompany = brand.substring(0, 1).toUpperCase() + brand.substring(1); // Output: "nissan"
                    }
                    int carModel = Integer.parseInt(carParts[0]);
//            Car car = new Car(carModel,carName,carCompany, transmission , price, imageLink, suv, seatCapacity, fuelEfficiency);
//public Car(int carModel, String name, String carCompany, String engineType, int price, String imageLink, String carType, String featureUrl, String features)
                    List<WebElement> carType = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[itemprop='name']")));
                    String description = carType.get(2).getText().trim();
                    String suv = "";
                    if (description.contains("SUV")) {
                        String[] part = description.split("&");
                        if (part.length > 1) {
                            suv = part[1].trim(); // Trim whitespace
                            suv = suv.replace("s", "");
                        }
                    } else {
                        if (description.equals("Cars")) {
                            suv = "Sedan";
                        } else {
                            suv = description;
                        }
                        if (suv.equals("Electric Cars")) {
                            if (carName.contains("LEAF")) {
                                suv = "Hatchback";
                            } else {
                                suv = "SUV";
                            }

                        }

                    }
                    if (suv.equals("Trucks")) {
                        suv = "Truck";
                    }

                    // Find price element with wait
                    List<WebElement> priceElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[class='price-item primary-price'] [class='price']")));
                    String price1 = priceElement.get(0).getText().trim();
                    // Clean and parse price
                    String cleanString = price1.replaceAll("[$,]", "");
                    int price = Integer.parseInt(cleanString);

                    WebElement element = driver.findElement(By.cssSelector("[class='accordion-button expand-button']"));

                    // Scroll the element into view
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

                    // Optionally adjust the scroll position if needed
                    ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, -200);"); // Scroll up by 200 pixels
                    // Wait for the element to be clickable
                    WebElement clickableElement = wait.until(ExpectedConditions.elementToBeClickable(element));

                    // Click on the element
                    clickableElement.sendKeys(Keys.ENTER);


                    // Find image element with wait
                    List<WebElement> img = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[class='vehicle-image'] img")));
                    String imageLink = img.get(0).getAttribute("src");
                    if (!dataValidation.isValidImageUrl(imageLink)) {
                        System.out.println("Not a valid image link");
                    }

                    // Find transmission element with wait
                    List<WebElement> trn = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[aria-controls='accordion-group-specifications-stats-panel-0']~[class='accordion-panel accordion-animation-complete'] tbody tr th")));
                    String transmission = trn.get(0).getText().trim();

                    // Find seat capacity element with wait
                    List<WebElement> seat = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[data-section-id='Seatingcapacity'] td")));
                    String seatCapacity = seat.get(0).getText().trim();

                    // Find fuel efficiency element with wait
                    String fuelEfficiency = null;
                    if (suv.contains("Truck")) {
                        List<WebElement> fuel = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[id='accordion-group-specifications-stats-panel-9'] tbody tr td")));
                        fuelEfficiency = fuel.get(6).getText().trim();
                    } else {
                        List<WebElement> fuel = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[id='accordion-group-specifications-stats-panel-8'] tbody tr td")));
                        fuelEfficiency = fuel.get(6).getText().trim();
                    }
                    fuelEfficiency=fuelEfficiency.replaceAll(".*?(\\d+\\.\\d+).*", "$1");
                    if(!dataValidation.isValidCarModel(carModel)){
                        System.out.println("Invalid car model: " + carModel);
                    }
                    if(!dataValidation.isValidCarCompany(carCompany)){
                        System.out.println("Invalid car company: " + carCompany);
                    }
                    if(!dataValidation.isValidCarName(carName)){
                        System.out.println("Invalid car name: " + carName);
                    }
                    if(!dataValidation.isValidPrice(price)){
                        System.out.println("Invalid car price: " + price);
                    }
                    if(!dataValidation.isValidFuelEfficiency(fuelEfficiency)){
                        System.out.println("Invalid car mileage: " + fuelEfficiency);
                    }
                    if(!dataValidation.isValidTransmission(transmission)){
                        System.out.println("Invalid car transmission: " + transmission);
                    }
                    if(!dataValidation.isValidSUV(suv)){
                        System.out.println("Invalid car type: " + suv);
                    }
                    // Create Car object and add to list
                    Car car = new Car(carModel, carName, carCompany, transmission, price, imageLink, suv, seatCapacity, fuelEfficiency);

                    cars.add(car);
                    car.printData();
                }


                // Quit the WebDriver
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            driver.quit();
        }
        return cars;

    }

}

