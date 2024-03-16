package com.example.carcomparision;

import java.io.Serializable;

public class Car implements Serializable {
    private String name;
    private int price;
    private String imageLink;
    private String carType;

    private int carModel;
    String seatCapacity;

    String range;

    private String carCompany;

    public String getSeatCapacity() {
        return seatCapacity;
    }

    public void setSeatCapacity(String seatCapacity) {
        this.seatCapacity = seatCapacity;
    }


    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public Car(String name, int price, String imageLink, String carType,String seatCapacity,String range,int carModel,String carCompany) {
        this.name = name;
        this.price = price;
        this.imageLink = imageLink;
        this.carType = carType;
        this.seatCapacity=seatCapacity;
        this.range=range;
        this.carModel=carModel;
        this.carCompany=carCompany;
    }

    // Getters and setters for all properties

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
