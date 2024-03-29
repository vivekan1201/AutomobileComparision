package com.example.carcomparision;

import java.io.Serializable;

public class Car implements Serializable {
    private String fuelEfficiency;
    private String seatCapacity;
    private String name;
    private String engineType;
    private int price;
    private int carModel;
    private String imageLink;
    private String carType;

    private String carCompany;
    int clickCount=0;

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public void incrementClickCount() {
        clickCount++;
    }


    public Car(int carModel, String name, String carCompany, String engineType, int price, String imageLink, String carType, String seatCapacity, String fuelEfficiency) {
        this.name = name;
        this.engineType = engineType;
        this.price = price;
        this.imageLink = imageLink;
        this.carType = carType;
        this.seatCapacity=seatCapacity;
        this.fuelEfficiency=fuelEfficiency;
        this.carModel=carModel;
        this.carCompany=carCompany;
    }

    // Getters and setters for all properties
    public int getCarModel() {
        return carModel;
    }

    public void setCarModel(int carModel) {
        this.carModel = carModel;
    }

    public String getCarCompany() {
        return carCompany;
    }

    public void setCarCompany(String carCompany) {
        this.carCompany = carCompany;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String description) {
        this.engineType = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
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

    public String getseatCapacity() {
        return seatCapacity;
    }


    public void setseatCapacity(String seatCapacity) {
        this.seatCapacity = seatCapacity;
    }
    public String getfuelEfficiency() {
        return fuelEfficiency;
    }


    public void setfuelEfficiency(String seatCapacity) {
        this.fuelEfficiency = fuelEfficiency;
    }
    public void printData() {
        System.out.println("Car Company: " + carCompany);
        System.out.println("Name: " + name);
        System.out.println("Model Year: "+ carModel);
        System.out.println("Engine type: " + engineType);
        System.out.println("Price: " + price);
        System.out.println("Image Link: " + imageLink);
        System.out.println("Car Type: " + carType);
        System.out.println("Seating capacity: " + seatCapacity);
        System.out.println("Fuel efficiency: "  + fuelEfficiency);
        System.out.println();
    }
}
