package com.example.carcomparision;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VocabularyService {

    private Set<String> vocabulary = new HashSet<>();
    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void init() {
        buildVocabularyFromFile("classpath:carWords.txt");
    }

    public List<String> suggestCorrections(String word) {
        List<String> suggestions = new ArrayList<>();

        // Check if the word is in the vocabulary
        if (vocabulary.contains(word)) {
           // suggestions.add(word); // If the word is correct, add it to suggestions
            return suggestions;
        }

        // Otherwise, find similar words in the vocabulary
        for (String vocabWord : vocabulary) {
            // Calculate Levenshtein distance between the input word and vocabulary word
            int distance = levenshteinDistance(word, vocabWord);
            // Use a threshold value to determine if the vocabulary word is a close match
            if (distance <= 2 && !word.equals(vocabWord)) { // Exclude the input word itself
                suggestions.add(vocabWord);
            }
        }

        return suggestions;
    }
    private int levenshteinDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i][j - 1], dp[i - 1][j]), dp[i - 1][j - 1]);
                }
            }
        }

        return dp[m][n];
    }
    private void buildVocabularyFromFile(String filePath) {
        vocabulary = new HashSet<>();
        try {
            InputStream inputStream = resourceLoader.getResource(filePath).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    vocabulary.add(word.toLowerCase());
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
