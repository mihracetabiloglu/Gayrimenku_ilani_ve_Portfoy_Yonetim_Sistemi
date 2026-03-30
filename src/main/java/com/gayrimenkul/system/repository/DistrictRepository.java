package com.gayrimenkul.system.repository;

import com.gayrimenkul.system.entity.District;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    // Şehir ID'sine göre tüm ilçeleri bulur
    List<District> findByCityId(Long cityId);
}
