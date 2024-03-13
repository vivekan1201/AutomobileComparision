package com.example.carcomparision;

import java.util.ArrayList;
import java.util.List;

public class CarBST {
    private Node root;

    private class Node {
        private Car car;
        private Node left, right;

        public Node(Car car) {
            this.car = car;
        }
    }

    public void insert(Car car) {
        root = insert(root, car);
    }

    private Node insert(Node node, Car car) {
        if (node == null) {
            return new Node(car);
        }

        int compare = Integer.compare(car.getPrice(), node.car.getPrice());
        if (compare < 0) {
            node.left = insert(node.left, car);
        } else if (compare > 0) {
            node.right = insert(node.right, car);
        }

        return node;
    }

    public List<Car> filterCars(String carType, int minPrice, int maxPrice) {
        List<Car> filteredCars = new ArrayList<>();
        filterCars(root, filteredCars, carType, minPrice, maxPrice);
        return filteredCars;
    }

    private void filterCars(Node node, List<Car> filteredCars, String carType, int minPrice, int maxPrice) {
        if (node == null) {
            return;
        }

        // In-order traversal to filter cars based on car type and price range
        filterCars(node.left, filteredCars, carType, minPrice, maxPrice);

        Car car = node.car;
        if ((carType == null || carType.equalsIgnoreCase(car.getCarType())) &&
                (minPrice <= car.getPrice() && car.getPrice() <= maxPrice)) {
            filteredCars.add(car);
        }

        filterCars(node.right, filteredCars, carType, minPrice, maxPrice);
    }
}
