package com.example.carcomparision;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class CarService {

    String connectionString = "mongodb://localhost:27017";
    String databaseName = "mydatabase";
    String collectionName = "carData";

    public void addCarsToDocument(List<Car> carsList) {

        // Set up MongoDB client
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            // Get a reference to the database
            MongoDatabase database = mongoClient.getDatabase(databaseName);

            // Get a reference to the collection
            MongoCollection<Document> collection = database.getCollection(collectionName);

            // Create a document to store the cars list
            Document document = new Document("_id", "scrapedCars");

            // Convert list of Car objects to a list of Document objects
            List<Document> carDocuments = new ArrayList<>();
            for (Car car : carsList) {
                Document carDocument = new Document();
                carDocument.append("name", car.getName());
                carDocument.append("description", car.getDescription());
                carDocument.append("price", car.getPrice());
                carDocument.append("imageLink", car.getImageLink());
                carDocument.append("carType", car.getCarType());
                carDocuments.add(carDocument);
            }

            // Add the list of car documents to the main document
            document.append("cars", carDocuments);

            // Insert the document into the collection
            collection.insertOne(document);

            System.out.println("Cars added to document 'scrapedCars' successfully.");
        }
    }
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
        String carDescription = car.getDescription().toLowerCase();
        double relevanceScore = 0.0;

        // Check if keyword matches car name or description
        if (carName.contains(keyword.toLowerCase())) {
            relevanceScore += 0.5; // Add partial relevance score for name match
        }
        if (carDescription.contains(keyword.toLowerCase())) {
            relevanceScore += 0.3; // Add partial relevance score for description match
        }

        return relevanceScore;
    }

    public int getFrequencyCount(String word, String url) {
        try {
            // Remove "file:///" prefix from the URL
            String filePath = url.replace("file:///", "");
            File file = new File(filePath);

            // Read the file
            Scanner scanner = new Scanner(file);
            int count = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().toLowerCase();
                String[] words = line.split("\\s+");
                for (String w : words) {
                    if (w.equals(word.toLowerCase())) {
                        count++;
                    }
                }
            }
            scanner.close();
            return count;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1; // Error reading the file
        }
    }


}
