package com.gayrimenkul.system.controller;

import com.gayrimenkul.system.entity.PropertyImage;
import com.gayrimenkul.system.service.PropertyImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping({"/api/properties/{propertyId}/images", "/api/listings/{propertyId}/images"})
@RequiredArgsConstructor
public class PropertyImageController {

    private final PropertyImageService propertyImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<List<PropertyImage>> uploadPropertyImages(
            @PathVariable Long propertyId,
            @RequestParam("images") List<MultipartFile> images) throws IOException {
        List<PropertyImage> savedImages = propertyImageService.savePropertyImages(propertyId, images);
        return new ResponseEntity<>(savedImages, HttpStatus.CREATED);
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Void> deletePropertyImage(
            @PathVariable Long propertyId,
            @PathVariable Long imageId) {
        propertyImageService.deletePropertyImage(propertyId, imageId);
        return ResponseEntity.noContent().build();
    }
}
