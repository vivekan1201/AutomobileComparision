package com.example.carcomparision;

public class CarTypeExtractor {
    public static void main(String[] args) {
        String description = "MAZDA CX-70 MILD HYBRID INLINE 6 TURBO\n2-Row SUV, Inline 6 Mild Hybrid - Starting at $49,750 1";
        String carType = extractCarType(description);
        System.out.println("Car Type: " + carType);
    }

    public static String extractCarType(String description) {
        // Split the description by comma to separate car type and other details
        String[] parts = description.split(",");
        // Extract the car type from the first part
        String carTypePart = parts[0].trim();
        // Split the car type part by space to get the last word which is the car type
        String[] words = carTypePart.split("\\s+");
        // Extract the last word as car type
        String carType = words[words.length - 1];
        return carType;
    }
}
