package com.example.carcomparision;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@RestController
public class WordCompletionController {

    private SplayTreeWordCompletion wordCompletion = new SplayTreeWordCompletion();

    public WordCompletionController() {
        // Initialize the SplayTreeWordCompletion instance by reading words from a file
        try (BufferedReader reader = new BufferedReader(new FileReader("D:\\University of windsor MAC Documents\\ACC\\ACC Final project\\Project\\AutomobileComparision\\src\\main\\resources\\carWords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordCompletion.insert(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/word-completions")
    public List<String> getWordCompletions(@RequestParam String prefix) {
        return wordCompletion.completeWord(prefix.toLowerCase());
    }
}