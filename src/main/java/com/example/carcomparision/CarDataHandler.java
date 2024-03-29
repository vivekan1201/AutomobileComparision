package com.example.carcomparision;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@Service
public class CarDataHandler {
    private static final String FILE_PATH = "C:\\Users\\shiva\\IdeaProjects\\AutomobileComparision\\src\\main\\resources\\topclicks.txt";

    public Map<String, Integer> readCarDataFromFile() {
        Map<String, Integer> carData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String carName = parts[0].trim();
                    int count = Integer.parseInt(parts[1].trim());
                    carData.put(carName, count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return carData;
    }

    public  void incrementCarCount(String carName, int count) {
        Map<String, Integer> carData = readCarDataFromFile();
        carData.put(carName, count);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<String, Integer> entry : carData.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
