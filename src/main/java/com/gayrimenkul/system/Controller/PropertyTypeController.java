package com.gayrimenkul.system.Controller;

import com.gayrimenkul.system.entity.PropertyType;
import com.gayrimenkul.system.service.PropertyTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/property-types")
@RequiredArgsConstructor
public class PropertyTypeController {

    private final PropertyTypeService propertyTypeService;

    @GetMapping
    public ResponseEntity<List<PropertyType>> getAllPropertyTypes() {
        return ResponseEntity.ok(propertyTypeService.getAllPropertyTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyType> getPropertyTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyTypeService.getPropertyTypeById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyType> createPropertyType(@RequestBody PropertyType propertyType) {
        PropertyType saved = propertyTypeService.createPropertyType(propertyType);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyType> updatePropertyType(@PathVariable Long id, @RequestBody PropertyType propertyType) {
        return ResponseEntity.ok(propertyTypeService.updatePropertyType(id, propertyType));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePropertyType(@PathVariable Long id) {
        propertyTypeService.deletePropertyType(id);
        return ResponseEntity.noContent().build();
    }
}
