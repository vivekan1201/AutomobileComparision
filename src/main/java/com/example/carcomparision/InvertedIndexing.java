package com.example.carcomparision;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
class InvertedIndex {
    private Map<String, List<Car>> index;

    @Autowired
    private CarService carService;



    private List<Car> carsData;

    public InvertedIndex() {
        index = new HashMap<>();

    }

    public void buildIndex(List<Car> cars) {
        carsData = cars;
        for (Car car : cars) {
            String[] terms = {
                    car.getName().toLowerCase(),
                    car.getCarCompany().toLowerCase(),
                    car.getEngineType().toLowerCase(),
                    car.getCarType().toLowerCase(),
                    car.getseatCapacity().toLowerCase(),
                    car.getfuelEfficiency().toLowerCase()
            };

            for (String term : terms) {
                index.putIfAbsent(term, new ArrayList<>());
                index.get(term).add(car);
            }
        }
    }

    public List<Car> search(String term) {
        List<Car> results = index.getOrDefault(term.toLowerCase(), new ArrayList<>());
        if (results.size() == 0) {
            results = carService.searchCars(term, carsData);
        }
        return results;
    }
}
// InvertedIndex invertedIndex = new InvertedIndex();
//        invertedIndex.buildIndex(cars);