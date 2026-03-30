package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.Property;
import com.gayrimenkul.system.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyService {

    private final PropertyRepository propertyRepository;

    // Yeni İlan Oluştur
    @Transactional
    public Property createProperty(Property property) {
        // İsteğe bağlı: Resimlerin property referanslarını bağlama döngüsü buraya eklenebilir
        if (property.getImages() != null) {
            property.getImages().forEach(img -> img.setProperty(property));
        }
        return propertyRepository.save(property);
    }

    // İlan Güncelle
    @Transactional
    public Property updateProperty(Long id, Property details) {
        Property existingProperty = getPropertyById(id);
        
        existingProperty.setTitle(details.getTitle());
        existingProperty.setDescription(details.getDescription());
        existingProperty.setPrice(details.getPrice());
        existingProperty.setCategory(details.getCategory());
        existingProperty.setCity(details.getCity());
        existingProperty.setDistrict(details.getDistrict());
        existingProperty.setPropertyType(details.getPropertyType());
        
        return propertyRepository.save(existingProperty);
    }

    // Tüm İlanları Getir
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // ID ile Detay Getir
    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İlan bulunamadı. ID: " + id));
    }

    // Filtreleyerek Getir
    public List<Property> searchProperties(Long cityId, Long categoryId, BigDecimal min, BigDecimal max) {
        return propertyRepository.filterProperties(cityId, categoryId, min, max);
    }

    // İlan Sil
    @Transactional
    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Silinmek istenen ilan bulunamadı.");
        }
        propertyRepository.deleteById(id);
    }
}