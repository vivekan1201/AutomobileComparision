package com.example.carcomparision;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cars")
public class CarController {


    private CarBST carBST = new CarBST();

    @Autowired
   private CarService carService;
    @Autowired
    private ObjectMapper objectMapper;

    private List<Car> cachedCars;

    @Autowired
    private VocabularyService vocabularyService;

@Autowired
ToyotaCarScraper toyotaCarScraper;

    private Map<String, Integer> searchFrequencyMap = new HashMap<>();
    private Map<String, Integer> frequencyCountMap = new HashMap<>();
    @Autowired
    NissanWebCrawling nissanWebCrawling;

    @Autowired
    HondaCarScraper hondaCarScraper;

    @Autowired
    MitsubishiCarScraping mitsubishiCarScraping;

    @Autowired
    InvertedIndex invertedIndex;


    @Autowired
    SplayTreeWordCompletion wordCompletion;

    private Map<String, List<String>> carModelsByCompany;

    @PostConstruct
    public void initialize() throws IOException, URISyntaxException, InterruptedException {
        // Initialize cachedCars and carModelsByCompany here
        cachedCars=nissanWebCrawling.getNissanCars();
        cachedCars=hondaCarScraper.getHondaCars(cachedCars);
        cachedCars=mitsubishiCarScraping.getMitsubishiCars(cachedCars);
        cachedCars=toyotaCarScraper.getToyotaCars(cachedCars);
        carModelsByCompany=createCarModelsByCompany(cachedCars);
        invertedIndex.buildIndex(cachedCars);
        carService.writeCarsToFile(cachedCars);

    }

    @GetMapping("/getAllCars")
    public List<Car> getAllCars() {
        return cachedCars;
    }


    @GetMapping("/filterCars")
    public List<Car> populateAndFilterCars(
            @RequestParam(value = "carType", required = false) String carType,
            @RequestParam(value = "minPrice", required = false) int minPrice,
            @RequestParam(value = "maxPrice", required = false) int maxPrice) {

        // Populate the CarBST with the retrieved cars
        for (Car car : cachedCars) {
            carBST.insert(car);
        }

        // Filter cars based on the provided parameters
        return carBST.filterCars(carType, minPrice, maxPrice);
    }

    @GetMapping("/search")
    public List<Car> searchCars(@RequestParam String keyword) {

        incrementSearchFrequency(keyword);
        // Retrieve relevant cars based on the search keyword
        //List<Car> relevantCars = carService.searchCars(keyword,cachedCars);
        List<Car> relevantCars=invertedIndex.search(keyword);
        // Increment frequency count for each searched term
        return relevantCars;
    }

    private void incrementSearchFrequency(String keyword) {
        searchFrequencyMap.put(keyword, searchFrequencyMap.getOrDefault(keyword, 0) + 1);
    }

    @GetMapping("/frequencyCount")
    public ResponseEntity<Integer> getFrequencyCount(@RequestParam String word, @RequestParam String url) {
        int count = carService.getFrequencyCount(word, url);
        if (count >= 0) {
            return ResponseEntity.ok(count);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1); // Error retrieving frequency count
        }
    }

    @GetMapping("/searchFrequency")
    public int getSearchFrequency(@RequestParam String word) {
        return searchFrequencyMap.getOrDefault(word, 0);
    }

    @GetMapping("/api/topSearches")
    public List<String> getTopSearches(@RequestParam(defaultValue = "5") int n) {
        return searchFrequencyMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @GetMapping("/spellcheck")
    public List<String> spellCheck(@RequestParam String word) {
        return vocabularyService.suggestCorrections(word);
    }

    @GetMapping("/companies")
    public Set<String> getExistingCarCompanies() {
        // Extract existing car companies from the keys of carModelsByCompany
        return carModelsByCompany.keySet();
    }
@GetMapping("/models")
public List<String> getCarModelsByCompany(@RequestParam String company) {
    return extractCarModels(company);
}

public Map<String, List<String>> createCarModelsByCompany(List<Car> cars) {
    Map<String, List<String>> map = new HashMap<>();
    for (Car car : cars) {
        String company = car.getCarCompany();
        String model = car.getName();
        map.computeIfAbsent(company, k -> new ArrayList<>()).add(model);
    }
    return map;
}

    @GetMapping("/compare")
    public ResponseEntity<Map<String, Car>> compareCars(
            @RequestParam String car1Name,
            @RequestParam String car2Name
    )
    {
        Car car1 = carService.findCarByName(car1Name,cachedCars);
        Car car2 = carService.findCarByName(car2Name,cachedCars);

        if (car1 == null || car2 == null) {
            // If either of the cars is not found, return a 404 Not Found response
            return ResponseEntity.notFound().build();
        }

        Map<String, Car> comparisonResult = new HashMap<>();
        comparisonResult.put("car1", car1);
        comparisonResult.put("car2", car2);

        return ResponseEntity.ok(comparisonResult);
    }

public List<String> extractCarModels(String company) {
    return carModelsByCompany.getOrDefault(company, Collections.emptyList());
}

    @GetMapping("/completeWord")
    public List<String> getCompleteWord(@RequestParam String prefix) {
        return wordCompletion.completeWordSuggestion(prefix);
    }

    @GetMapping("/getCarByName")
    public Car getCarByName(@RequestParam String carName) {
        Car selectedCar = carService.findCarByName(carName,cachedCars);
        return selectedCar;
    }


}




