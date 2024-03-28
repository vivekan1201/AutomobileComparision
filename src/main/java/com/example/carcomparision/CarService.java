package com.example.carcomparision;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CarService {

    String connectionString = "mongodb://localhost:27017";
    String databaseName = "mydatabase";
    String collectionName = "carData";



    public Document getDocumentById(String documentId) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            // Get a reference to the database
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            // Get a reference to the collection
            MongoCollection<Document> collection = database.getCollection(collectionName);

            Document query = new Document("_id", documentId);
            Document result = collection.find(query).first();
            if (result != null) {
                return result;
            } else {
                System.out.println("Document not found with ID: " + documentId);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error getting document: " + e.getMessage());
            return null;
        }
    }

    public List<Car> searchCars(String keyword, List<Car> carList) {
        // Perform search logic to retrieve relevant car objects based on the keyword

        // Calculate relevance scores for each car object based on the keyword
        Map<Car, Double> relevanceMap = new HashMap<>();
        for (Car car : carList) {
            double relevanceScore = calculateRelevanceScore(car, keyword);
            relevanceMap.put(car, relevanceScore); // Store relevance score for each car
        }

        // Define a threshold for relevance score
        double threshold = 0.5; // Adjust the threshold as needed

        // Filter relevant cars based on the threshold
        List<Car> relevantCars = new ArrayList<>();
        for (Map.Entry<Car, Double> entry : relevanceMap.entrySet()) {
            if (entry.getValue() >= threshold) {
                relevantCars.add(entry.getKey());
            }
        }

        // Sort relevant cars based on relevance score (optional)
        relevantCars.sort(Comparator.comparingDouble(relevanceMap::get).reversed());

        return relevantCars;
    }


    private double calculateRelevanceScore(Car car, String keyword) {
        // Calculate relevance score based on various factors such as car attributes, keyword match, etc.
        // You can use any suitable algorithm here

        // Example: Simple relevance score based on keyword matching in car name and description
        String carName = car.getName().toLowerCase();
//        String carDescription = car.getDescription().toLowerCase();
        double relevanceScore = 0.0;

        // Check if keyword matches car name or description
        if (carName.contains(keyword.toLowerCase())) {
            relevanceScore += 0.5; // Add partial relevance score for name match
        }
//        if (carDescription.contains(keyword.toLowerCase())) {
//            relevanceScore += 0.3; // Add partial relevance score for description match
//        }

        return relevanceScore;
    }

    public int getFrequencyCount(String word, String url) {
        WebDriver driver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // Run in headless mode
            driver = new ChromeDriver(options);
            driver.get(url);

            // Wait for the page to fully load and execute JavaScript
            WebElement bodyElement = driver.findElement(By.tagName("body"));

// Get the text content of the body element
            String pageText = bodyElement.getText();

            // Count occurrences of each word
            int frequency = 0;
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(word) + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(pageText);

            while (matcher.find()) {
                frequency++;
            }
            driver.quit();
            return frequency;


        } finally {

        }


    }

    Car findCarByName(String carName, List<Car> cachedCars) {
        for (Car car : cachedCars) {
            if (car.getName().equalsIgnoreCase(carName)) {
                return car;
            }
        }
        return null; // Car not found
    }

    public void writeCarsToFile(List<Car> carList) {

        String filePath="C:\\Users\\shiva\\IdeaProjects\\AutomobileComparision\\src\\main\\resources\\carWords.txt";
        try {
            // Create a FileWriter object
            FileWriter writer = new FileWriter(filePath);

            // Write car details to the file
            for (Car car : carList) {
                // Split name and engine type into individual words
                String[] nameWords = car.getName().split("\\s+");
                String[] engineTypeWords = car.getEngineType().split("\\s+");

                // Write each word to the file
                for (String word : nameWords) {
                    writer.write(word + "\n");
                }
                for (String word : engineTypeWords) {
                    writer.write(word + "\n");
                }

                // Write company to the file
                writer.write(car.getCarCompany() + "\n");
            }

            // Close the writer
            writer.close();
            System.out.println("Car details have been written to the file successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}