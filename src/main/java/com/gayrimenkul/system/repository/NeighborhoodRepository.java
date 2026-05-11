package com.gayrimenkul.system.repository;

import com.gayrimenkul.system.entity.Neighborhood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NeighborhoodRepository extends JpaRepository<Neighborhood, Long> {
    List<Neighborhood> findByDistrictId(Long districtId);
    long countByDistrictId(Long districtId);
}
