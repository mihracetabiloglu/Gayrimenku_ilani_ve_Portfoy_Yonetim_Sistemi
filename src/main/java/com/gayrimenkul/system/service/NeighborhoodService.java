package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.Neighborhood;
import com.gayrimenkul.system.repository.NeighborhoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NeighborhoodService {
    private final NeighborhoodRepository neighborhoodRepository;

    public NeighborhoodService(NeighborhoodRepository neighborhoodRepository) {
        this.neighborhoodRepository = neighborhoodRepository;
    }

    public List<Neighborhood> getAllNeighborhoods() {
        return neighborhoodRepository.findAll();
    }

    public Neighborhood saveNeighborhood(Neighborhood neighborhood) {
        return neighborhoodRepository.save(neighborhood);
    }

    public void deleteNeighborhood(Long id) {
        neighborhoodRepository.deleteById(id);
    }
}