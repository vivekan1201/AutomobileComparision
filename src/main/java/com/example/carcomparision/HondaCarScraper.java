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

import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class HondaCarScraper {
    private CarBST carBST = new CarBST();
    @Autowired
    private CarService carService;
    @Autowired
    private ObjectMapper objectMapper;
    private static List<Car> cachedCars;
    private Map<String, Integer> searchFrequencyMap = new HashMap<>();
    private Map<String, Integer> frequencyCountMap = new HashMap<>();

    // Set up Chrome WebDriver
    public List<Car> getHondaCars (List<Car> cars) throws InterruptedException, URISyntaxException, MalformedURLException {
        // URLs to scrape
        String[] urls = {
                "https://www.honda.ca/en/crv/specs",
                "https://www.honda.ca/en/civic_sedan/specs",
                "https://www.honda.ca/en/accord/specs",
                "https://www.honda.ca/en/hrv/specs",
                "https://www.honda.ca/en/passport/specs",
                "https://www.honda.ca/en/pilot/specs",
                "https://www.honda.ca/en/ridgeline/specs",
                "https://www.honda.ca/en/odyssey/specs"

        };

        // Set up Chrome WebDriver
        String chromeDriverPath = "C:\\Users\\visha\\Desktop\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        // Initialize ChromeDriver options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode
        WebDriver driver = new ChromeDriver(options);
//            driver.manage().window().maximize();
        boolean isFirstIteration = true;
        try {
            // Iterate through each URL and scrape car information
            for (String url : urls) {
                if(dataValidation.isValidURL(url)) {
                    driver.get(url);
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    if (isFirstIteration) {
                        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='close-button']")));
                        closeButton.sendKeys(Keys.ENTER);
                        isFirstIteration = false; // Set the flag to false after the first iteration
                    }
                    List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[class='english no-touchevents'] head title")));

                    // Extract the text content from the element
                    String textContent = elements.get(0).getAttribute("innerHTML");
                    Pattern pattern = Pattern.compile("(\\d{4})\\s+(.*?)\\s+(\\w+(?:-\\w+)*)(?:\\s+([^:]*?)\\s*:)?.*");

                    // Match the pattern against the text
                    Matcher matcher = pattern.matcher(textContent);

                    // Check if the pattern matches
                    if (matcher.matches()) {
                        // Extract the matched groups
                        String year = matcher.group(1);
                        String manufacturer = matcher.group(2);
                        String model = matcher.group(3);
                        String carType = matcher.group(4);

                        // Extract only the base type of the car
                        if (carType != null) {
                            String[] words = carType.split("\\s+");
                            carType = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", ""); // Extract the last word
                        }

                        // Print the extracted information
                        int carModel = Integer.parseInt(year);
                        if(!dataValidation.isValidCarModel(carModel)){
                            System.out.println("Invalid car model: " + carModel);
                        }
                        String carCompany = manufacturer;
                        String carName = model.trim();
                        if(!dataValidation.isValidCarName(carName)){
                            System.out.println("Invalid car name: " + carName);
                        }
                        String suv;
                        if (Objects.equals(carType, null)) {
                            suv = "SUV";
                        } else {
                            suv = carType;
                        }
                        if(!dataValidation.isValidSUV(suv)){
                            System.out.println("Invalid car type: " + suv);
                        }


                        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[aria-label='Expand All']")));
                        if (element != null) {
                            element.click(); // Click on the element
                        } else {
                            System.out.println("No element found with aria-label='Expand All'");
                        }

//            price
                        List<WebElement> priceElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[data-testid='cy-pricing-price']")));
                        String price1 = priceElement.get(0).getText().trim();
                        // Clean and parse price
                        String cleanString = price1.replaceAll("[$,]", "");
                        int price = Integer.parseInt(cleanString);
                        if(!dataValidation.isValidPrice(price)){
                            System.out.println("Invalid car price: " + price);
                        }

                        List<WebElement> img = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[data-testid='cy-trimcard-image-wrapper'] img")));

                        String imageLink = img.get(0).getAttribute("src");
                        if(!dataValidation.isValidImageUrl(imageLink)){
                            System.out.println("Not a valid image link");
                        }

                        List<WebElement> engine = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[data-testid='dsr-accordion-item-container']")));
                        String enginetext = engine.get(0).getText();
                        Pattern pattern5 = Pattern.compile("ENGINE\n(.*?)\\n.*", Pattern.DOTALL);

                        // Create a matcher for the given text
                        Matcher matcher5 = pattern5.matcher(enginetext);

                        // Find the first occurrence of the pattern
                        String engineDescription = null;
                        if (matcher5.find()) {
                            engineDescription = matcher5.group(1).trim();
//                    System.out.println("Engine Description: " + engineDescription);
                        }
                        String transmission = engineDescription;
                        if(!dataValidation.isValidTransmission(transmission)){
                            System.out.println("Invalid car transmission: " + transmission);
                        }
                        //mileage
                        List<WebElement> mileage = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[data-testid='dsr-accordion-item-container']")));
                        String mileage1 = mileage.get(10).getText();
                        Pattern pattern1 = Pattern.compile("\\d+\\.\\d+");

                        // Create a matcher for the given text
                        Matcher matcher1 = pattern1.matcher(mileage1);

                        // Find the first occurrence of the pattern
                        String fuelEconomy = null;
                        if (matcher1.find()) {
                            fuelEconomy = matcher1.group();
                        }
                        String fuelEfficiency = fuelEconomy;
                        if(!dataValidation.isValidFuelEfficiency(fuelEfficiency)){
                            System.out.println("Invalid car mileage: " + fuelEfficiency);
                        }

                        //seat capacity
                        List<WebElement> seat = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[data-testid='dsr-accordion-item-container']")));
                        String seat1 = seat.get(8).getText();
                        Pattern pattern2 = Pattern.compile(" (?=\\d+\\n)");


                        // Create a matcher for the given text
                        Matcher matcher2 = pattern2.matcher(seat1);

                        // Find the first occurrence of the pattern
                        int seat12;
                        if (matcher2.find()) {
                            String seatingCapacity = matcher2.group().trim();
                            if (!seatingCapacity.isEmpty()) {
                                seat12 = Integer.parseInt(seatingCapacity);
                                if (seat12 > 7 || seat12 == 0) {
                                    seat12 = 5;
                                }
                            } else {
                                seat12 = 5;
                            }
                        } else {
                            seat12 = 5;
                        }
                        String seatCapacity = String.valueOf(seat12);
                        if(!dataValidation.isValidSeatCapacity(seatCapacity)){
                            System.out.println("Invalid car seating capacity: " + seatCapacity);
                        }
                        Car car = new Car(carModel, carName, carCompany, transmission, price, imageLink, suv, seatCapacity, fuelEfficiency);
                        cars.add(car);
                        car.printData();

                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            driver.quit();
        }
        return cars;
    }
}
