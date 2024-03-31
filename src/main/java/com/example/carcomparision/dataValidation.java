package com.example.carcomparision;

public class dataValidation {
    public static boolean isValidImageUrl(String imageUrl) {
        String imageRegex = "^https?://.*\\.(jpg|jpeg|png|gif)(\\?.*)?$";
        return imageUrl.matches(imageRegex);
    }

    public static boolean isValidURL(String url) {
        String urlRegex = "^https?:\\/\\/[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$";
        return url.matches(urlRegex);
    }
    public static boolean isValidCarModel(int carModel) {
        // Check if car model is a 4-digit year
        return String.valueOf(carModel).matches("^\\d{4}$");
    }

    public static boolean isValidCarName(String carName) {
        // Check if car name contains only alphanumeric characters and hyphens
        return carName.matches("^[a-zA-Z0-9\\-\\s]+$");
    }

    public static boolean isValidCarCompany(String carCompany) {
        // Check if car company contains only alphanumeric characters
        return carCompany.matches("^[a-zA-Z0-9]+$");
    }

    public static boolean isValidTransmission(String transmission) {
        // Transmission can contain any characters
        return true; // No validation needed for transmission
    }

    public static boolean isValidPrice(int price) {
        // Check if price is a 5 to 7-digit integer
        return String.valueOf(price).matches("^\\d{5,7}$");
    }

    public static boolean isValidSUV(String suv) {
        // Check if suv contains only alphabets with 2 words max
        return suv.matches("^[a-zA-Z]+(\\s[a-zA-Z]+)?$");
    }

    public static boolean isValidSeatCapacity(String seatCapacity) {
        // Convert seat capacity to int and check if it's between 2 and 8
        try {
            int seatCapacityInt = Integer.parseInt(seatCapacity);
            return seatCapacityInt >= 2 && seatCapacityInt <= 8;
        } catch (NumberFormatException e) {
            return false; // Return false if seatCapacity is not a valid integer
        }
    }

    public static boolean isValidFuelEfficiency(String fuelEfficiency) {
        // Check if fuel efficiency is a decimal between 1.0 and 30.0
        return fuelEfficiency.matches("^(1\\.\\d+|\\d{1,2}(\\.\\d+)?)$") && Double.parseDouble(fuelEfficiency) >= 1.0 && Double.parseDouble(fuelEfficiency) <= 30.0;
    }

    public static boolean isValidSearchInput(String input) {
        // Regular expression allowing alphanumeric characters, spaces, and some special characters
        String regex = "^[a-zA-Z0-9\\s.,!?()-]+$";
        return input != null && !input.trim().isEmpty() && input.matches(regex);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean containsOnlyWhitespace(String str) {
        return str != null && str.trim().isEmpty();
    }

}
