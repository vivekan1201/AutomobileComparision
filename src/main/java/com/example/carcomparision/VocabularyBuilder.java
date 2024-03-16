package com.example.carcomparision;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class VocabularyBuilder {
    public static Set<String> buildVocabulary(String filePath) {
        Set<String> vocabulary = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Preprocess the text: remove punctuation and convert to lowercase
                line = line.replaceAll("[^a-zA-Z ]", "").toLowerCase();
                // Split the line into words
                String[] words = line.split("\\s+");
                // Add each word to the vocabulary set
                for (String word : words) {
                    if (!word.isEmpty()) {
                        vocabulary.add(word);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vocabulary;
    }
}
