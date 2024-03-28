package com.example.carcomparision;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SplayTreeNode {
    String word;
    SplayTreeNode left, right;

    public SplayTreeNode(String word) {
        this.word = word;
        this.left = null;
        this.right = null;
    }
}
@Service
public class SplayTreeWordCompletion {
    private SplayTreeNode root;

    public SplayTreeWordCompletion() {
        root = null;
    }

    public void insert(String word) {
        root = splay(root, word);
        if (root == null || !root.word.equals(word)) {
            SplayTreeNode newNode = new SplayTreeNode(word);
            if (root == null) {
                root = newNode;
            } else if (root.word.compareTo(word) > 0) {
                newNode.right = root;
                newNode.left = root.left;
                root.left = null;
                root = newNode;
            } else {
                newNode.left = root;
                newNode.right = root.right;
                root.right = null;
                root = newNode;
            }
        }
    }

    private SplayTreeNode splay(SplayTreeNode node, String word) {
        if (node == null || node.word.equals(word)) {
            return node;
        }

        if (node.word.compareTo(word) > 0) {
            if (node.left == null) {
                return node;
            }
            if (node.left.word.compareTo(word) > 0) {
                node.left.left = splay(node.left.left, word);
                node = rotateRight(node);
            } else if (node.left.word.compareTo(word) < 0) {
                node.left.right = splay(node.left.right, word);
                if (node.left.right != null) {
                    node.left = rotateLeft(node.left);
                }
            }
            return (node.left == null) ? node : rotateRight(node);
        } else {
            if (node.right == null) {
                return node;
            }
            if (node.right.word.compareTo(word) > 0) {
                node.right.left = splay(node.right.left, word);
                if (node.right.left != null) {
                    node.right = rotateRight(node.right);
                }
            } else if (node.right.word.compareTo(word) < 0) {
                node.right.right = splay(node.right.right, word);
                node = rotateLeft(node);
            }
            return (node.right == null) ? node : rotateLeft(node);
        }
    }

    private SplayTreeNode rotateRight(SplayTreeNode node) {
        SplayTreeNode temp = node.left;
        node.left = temp.right;
        temp.right = node;
        return temp;
    }

    private SplayTreeNode rotateLeft(SplayTreeNode node) {
        SplayTreeNode temp = node.right;
        node.right = temp.left;
        temp.left = node;
        return temp;
    }

    public List<String> completeWord(String prefix) {
        prefix = prefix.toLowerCase(); // Convert prefix to lowercase
        List<String> completions = new ArrayList<>();
        SplayTreeNode node = root;
        while (node != null) {
            if (node.word.toLowerCase().startsWith(prefix)) { // Convert node word to lowercase
                completions.add(node.word);
            }
            if (node.word.compareTo(prefix) < 0) {
                node = node.right;
            } else {
                node = node.left;
            }
        }
        return completions;
    }


    public List<String> completeWordSuggestion(String prefix){
        SplayTreeWordCompletion wordCompletion = new SplayTreeWordCompletion();
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\shiva\\IdeaProjects\\AutomobileComparision\\src\\main\\resources\\carWords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordCompletion.insert(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> completions = wordCompletion.completeWord(prefix);
        System.out.println("Word completions for prefix '" + prefix + "':");

        return completions;

    }
}
