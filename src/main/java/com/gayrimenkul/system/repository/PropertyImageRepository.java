package com.gayrimenkul.system.repository;

import com.gayrimenkul.system.entity.PropertyImage;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
    List<PropertyImage> findByPropertyId(Long propertyId);
    Optional<PropertyImage> findByIdAndPropertyId(Long id, Long propertyId);
}
