package com.example.carcomparision;

import java.util.*;

public class SpellChecker {
    private Set<String> vocabulary;

    public SpellChecker(Set<String> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public List<String> suggestCorrections(String word) {
        List<String> suggestions = new ArrayList<>();

        // Check if the word is in the vocabulary
        if (vocabulary.contains(word)) {
            suggestions.add(word); // If the word is correct, add it to suggestions
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

    // Helper method to calculate Levenshtein distance between two strings
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

    // Example usage
    public static void main(String[] args) {
        // Example vocabulary
        Set<String> vocabulary = new HashSet<>();
        vocabulary.add("car");
        vocabulary.add("card");
        vocabulary.add("care");
        vocabulary.add("mazdas");
        vocabulary.add("mazdi");

        // Initialize spell checker with the vocabulary
        SpellChecker spellChecker = new SpellChecker(vocabulary);

        // Get suggestions for a misspelled word
        List<String> suggestions = spellChecker.suggestCorrections("maz");
        System.out.println("Suggestions for 'car': " + suggestions);
    }
}
