package com.gayrimenkul.system.Controller;

import com.gayrimenkul.system.entity.Neighborhood;
import com.gayrimenkul.system.service.NeighborhoodService;
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

    @PostMapping
    public Neighborhood saveNeighborhood(@RequestBody Neighborhood neighborhood) {
        return neighborhoodService.saveNeighborhood(neighborhood);
    }

    @DeleteMapping("/{id}")
    public void deleteNeighborhood(@PathVariable Long id) {
        neighborhoodService.deleteNeighborhood(id);
    }
}