package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.PropertyType;
import com.gayrimenkul.system.repository.PropertyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyTypeService {

    private final PropertyTypeRepository propertyTypeRepository;

    // Tüm Emlak Tiplerini Getir
    @Transactional(readOnly = true)
    public List<PropertyType> getAllPropertyTypes() {
        return propertyTypeRepository.findAll();
    }

    // ID ile Getir
    @Transactional(readOnly = true)
    public PropertyType getPropertyTypeById(Long id) {
        return propertyTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emlak tipi bulunamadı! ID: " + id));
    }

    // Yeni Emlak Tipi Oluştur
    @Transactional
    public PropertyType createPropertyType(PropertyType propertyType) {
        return propertyTypeRepository.save(propertyType);
    }

    // Güncelle
    @Transactional
    public PropertyType updatePropertyType(Long id, PropertyType updatedType) {
        PropertyType existingType = getPropertyTypeById(id);
        
        existingType.setName(updatedType.getName());
        existingType.setCreatedBy(updatedType.getCreatedBy());
        
        return propertyTypeRepository.save(existingType);
    }

    // Sil
    @Transactional
    public void deletePropertyType(Long id) {
        if (!propertyTypeRepository.existsById(id)) {
            throw new RuntimeException("Silinmek istenen tip mevcut değil.");
        }
        propertyTypeRepository.deleteById(id);
    }
}