package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.Floor;
import com.gayrimenkul.system.repository.FloorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FloorService {
    private final FloorRepository floorRepository;

    public FloorService(FloorRepository floorRepository) {
        this.floorRepository = floorRepository;
    }

    public List<Floor> getAllFloors() {
        return floorRepository.findAll();
    }

    public Floor saveFloor(Floor floor) {
        return floorRepository.save(floor);
    }

    public void deleteFloor(Long id) {
        floorRepository.deleteById(id);
    }
}