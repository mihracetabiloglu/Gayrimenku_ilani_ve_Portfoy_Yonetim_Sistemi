package com.gayrimenkul.system.Controller;

import com.gayrimenkul.system.entity.City;
import com.gayrimenkul.system.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    // 1. Tüm şehirleri getir
    @GetMapping
    public ResponseEntity<List<City>> getAllCities() {
        List<City> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }

    // 2. ID'ye göre tek bir şehir getir
    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable Long id) {
        City city = cityService.getCityById(id);
        return ResponseEntity.ok(city);
    }

    // 3. Yeni şehir ekle
    @PostMapping
    public ResponseEntity<City> addCity(@RequestBody City city) {
        // Yeni kayıtlarda ID'nin boş olduğundan emin olmak iyi bir güvenlik pratiğidir
        city.setId(null); 
        City savedCity = cityService.saveCity(city);
        return new ResponseEntity<>(savedCity, HttpStatus.CREATED); // 201 Created
    }

    // 4. Şehir güncelle
    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(@PathVariable Long id, @RequestBody City city) {
        // URL'den gelen ID ile Body'den gelen nesneyi eşleştiriyoruz
        city.setId(id);
        City updatedCity = cityService.saveCity(city);
        return ResponseEntity.ok(updatedCity);
    }

    // 5. Şehir sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}