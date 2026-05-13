package com.gayrimenkul.system.controller;

import com.gayrimenkul.system.entity.Neighborhood;
import com.gayrimenkul.system.service.NeighborhoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/neighborhoods")
public class NeighborhoodController {
    private final NeighborhoodService neighborhoodService;

    public NeighborhoodController(NeighborhoodService neighborhoodService) {
        this.neighborhoodService = neighborhoodService;
    }

    @GetMapping
    public List<Neighborhood> getAllNeighborhoods() {
        return neighborhoodService.getAllNeighborhoods();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Neighborhood> getNeighborhoodById(@PathVariable Long id) {
        return ResponseEntity.ok(neighborhoodService.getNeighborhoodById(id));
    }

    @GetMapping("/district/{districtId}")
    public List<Neighborhood> getNeighborhoodsByDistrictId(@PathVariable Long districtId) {
        return neighborhoodService.getNeighborhoodsByDistrictId(districtId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Neighborhood> saveNeighborhood(@RequestBody Neighborhood neighborhood) {
        return new ResponseEntity<>(neighborhoodService.saveNeighborhood(neighborhood), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Neighborhood> updateNeighborhood(@PathVariable Long id, @RequestBody Neighborhood neighborhood) {
        return ResponseEntity.ok(neighborhoodService.updateNeighborhood(id, neighborhood));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Neighborhood> patchNeighborhood(@PathVariable Long id, @RequestBody Neighborhood neighborhood) {
        return ResponseEntity.ok(neighborhoodService.updateNeighborhood(id, neighborhood));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNeighborhood(@PathVariable Long id) {
        neighborhoodService.deleteNeighborhood(id);
        return ResponseEntity.noContent().build();
    }
}
