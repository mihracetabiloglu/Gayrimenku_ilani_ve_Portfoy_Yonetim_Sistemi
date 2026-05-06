package com.gayrimenkul.system.Controller;

import com.gayrimenkul.system.entity.District;
import com.gayrimenkul.system.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/districts")
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @GetMapping
    public ResponseEntity<List<District>> getAllDistricts() {
        return ResponseEntity.ok(districtService.getAllDistricts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<District> getDistrictById(@PathVariable Long id) {
        return ResponseEntity.ok(districtService.getDistrictById(id));
    }

    @GetMapping("/by-city/{cityId}")
    public ResponseEntity<List<District>> getDistrictsByCityId(@PathVariable Long cityId) {
        return ResponseEntity.ok(districtService.getDistrictsByCityId(cityId));
    }

    @PostMapping
    public ResponseEntity<District> addDistrict(@RequestBody District district) {
        District savedDistrict = districtService.addDistrict(district);
        return new ResponseEntity<>(savedDistrict, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<District> updateDistrict(@PathVariable Long id, @RequestBody District district) {
        return ResponseEntity.ok(districtService.updateDistrict(id, district));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDistrict(@PathVariable Long id) {
        districtService.deleteDistrict(id);
        return ResponseEntity.noContent().build();
    }
}
