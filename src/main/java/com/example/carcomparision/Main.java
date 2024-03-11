package com.example.carcomparision;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        // Set the path to your ChromeDriver executable
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

        // Find and return the dynamic data
//        WebElement dynamicElement = driver.findElement(By.cssSelector(".mz-tabs__content .mz-jelly-content div"));
//        List<WebElement> elements = driver.findElements(By.cssSelector(".mz-tabs__content .mz-jelly-content"));
//        for (WebElement element : elements) {
//            String elementText = element.getText();
////            System.out.println(elementText);
//            extractCarDetails(elementText);
//        }
//
//        // Close the browser
//        driver.quit();
        List<WebElement> carElements = driver.findElements(By.className("mz-tabs__list"));

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>");
        htmlContent.append("<html lang=\"en\">");
        htmlContent.append("<head>");
        htmlContent.append("<meta charset=\"UTF-8\">");
        htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        htmlContent.append("<title>Car Details</title>");
        htmlContent.append("<style>");
        htmlContent.append(".container { max-width: 800px; margin: 0 auto; padding: 20px; }");
        htmlContent.append(".car { display: flex; border: 1px solid #ccc; margin-bottom: 20px; }");
        htmlContent.append(".car img { width: 200px; height: auto; }");
        htmlContent.append(".car-info { padding: 20px; }");
        htmlContent.append(".price { font-weight: bold; }");
        htmlContent.append(".row { display: flex; flex-wrap: wrap; justify-content: space-between; }");
        htmlContent.append("</style>");
        htmlContent.append("</head>");
        htmlContent.append("<body>");
        htmlContent.append("<div class=\"container\">");
        htmlContent.append("<h1 style=\"text-align: center;\">Latest Cars</h1>");

        // Initialize a counter to keep track of the number of cars displayed
        int carCount = 0;

        // Create a row container for the cars
        htmlContent.append("<div class=\"row\">");

        // Iterate through each car element and extract details
        for (WebElement carElement : carElements) {
            // Extract car name
            WebElement nameElement = carElement.findElement(By.tagName("h5"));
            String carName = nameElement.getText().trim();

            // If car name is null, skip this car
            if (carName.isEmpty()) {
                continue;
            }

            // Extract car description (price and type)
            WebElement descriptionElement = carElement.findElement(By.xpath(".//div[contains(@class, 'mz-jelly-content')]"));
            String carDescription = descriptionElement.getText().trim();

            // Extract price
            String price = extractPrice(carDescription);

            WebElement imageElement = carElement.findElement(By.tagName("img"));
            String imageLink = imageElement.getAttribute("src");

            // Append the car details to the HTML content
            htmlContent.append("<div class=\"car\">");
            htmlContent.append("<img src=\"" + imageLink + "\" alt=\"" + carName + "\">");
            htmlContent.append("<div class=\"car-info\">");
            htmlContent.append("<h2>" + carName + "</h2>");
            htmlContent.append("<p>" + carDescription + "</p>");
            htmlContent.append("<p class=\"price\">" + price + "</p>");
            htmlContent.append("</div>");
            htmlContent.append("</div>");

            // Increment the car count
            carCount++;

            // Check if the maximum number of cars per row is reached
            if (carCount % 4 == 0) {
                // Close the current row and start a new one
                htmlContent.append("</div><div class=\"row\">");
            }
        }

        // Close the row container
        htmlContent.append("</div>");

        htmlContent.append("</div>");
        htmlContent.append("</body>");
        htmlContent.append("</html>");

        // Quit the driver
        driver.quit();

        // Print the generated HTML content
        System.out.println(htmlContent.toString());
        writeToFile(htmlContent.toString(),"car_details.html");
    }

    // Method to write content to a file
    public static void writeToFile(String content, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String extractPrice(String carDescription) {
        // Use regex to extract the price pattern from the description
        String regex = "\\$[0-9,]+";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(carDescription);

        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return "Price not found";
        }
    }

    private static void extractCarDetails(String text) {
        String[] lines = text.split("\n");

        if (lines.length >= 2) {
            String carName = lines[0].trim();
            String detailsLine = lines[1].trim();

            // Extracting details from the second line
            String[] detailsArray = detailsLine.split(" - ");
            String carType = detailsArray[0].trim();
            String price = detailsArray[1].trim().split(" ")[2];

            // Print the extracted details
            System.out.println("Car Name: " + carName);
            System.out.println("Car Type: " + carType);
            System.out.println("Price: " + price);
            System.out.println();
        } else {
            System.out.println("Invalid format: " + text);
        }
    }
}
