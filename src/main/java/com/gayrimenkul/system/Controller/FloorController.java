package com.gayrimenkul.system.Controller;

import com.gayrimenkul.system.entity.Floor;
import com.gayrimenkul.system.service.FloorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/floors")
public class FloorController {
    private final FloorService floorService;

    public FloorController(FloorService floorService) {
        this.floorService = floorService;
    }

    @GetMapping
    public List<Floor> getAllFloors() {
        return floorService.getAllFloors();
    }

    @PostMapping
    public Floor saveFloor(@RequestBody Floor floor) {
        return floorService.saveFloor(floor);
    }

    @DeleteMapping("/{id}")
    public void deleteFloor(@PathVariable Long id) {
        floorService.deleteFloor(id);
    }
}