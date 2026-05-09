package com.gayrimenkul.system.Controller;

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
@RequestMapping("/api/properties/{propertyId}/images")
@RequiredArgsConstructor
public class PropertyImageController {

    private final PropertyImageService propertyImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Void> uploadPropertyImages(
            @PathVariable Long propertyId,
            @RequestPart("images") List<MultipartFile> images) throws IOException {
        propertyImageService.savePropertyImages(propertyId, images);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
