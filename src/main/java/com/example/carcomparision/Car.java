package com.example.carcomparision;

import java.io.Serializable;

public class Car implements Serializable {
    private String name;
    private String description;
    private String price;
    private String imageLink;
    private String carType;

    public Car(String name, String description, String price, String imageLink, String carType) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageLink = imageLink;
        this.carType = carType;
    }

    // Getters and setters for all properties

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }
}
