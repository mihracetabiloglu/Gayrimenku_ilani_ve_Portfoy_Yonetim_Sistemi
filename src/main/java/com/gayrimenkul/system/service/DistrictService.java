package com.gayrimenkul.system.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gayrimenkul.system.entity.District;
import com.gayrimenkul.system.repository.DistrictRepository;
import com.gayrimenkul.system.repository.NeighborhoodRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DistrictService {

    private final DistrictRepository districtRepository;
    private final NeighborhoodRepository neighborhoodRepository;

    @Transactional
    public District addDistrict(District district) {
        if (district.getActive() == null) {
            district.setActive(true);
        }
        return withNeighborhoodCount(districtRepository.save(district));
    }

    @Transactional
    public District updateDistrict(Long id, District updatedDistrict) {
        return districtRepository.findById(id)
                .map(district -> {
                    district.setName(updatedDistrict.getName());
                    district.setCity(updatedDistrict.getCity());
                    if (updatedDistrict.getActive() != null) {
                        district.setActive(updatedDistrict.getActive());
                    }
                    return withNeighborhoodCount(districtRepository.save(district));
                })
                .orElseThrow(() -> new RuntimeException("Ilce bulunamadi"));
    }

    @Transactional
    public void deleteDistrict(Long id) {
        if (!districtRepository.existsById(id)) {
            throw new RuntimeException("Ilce bulunamadi");
        }
        districtRepository.deleteById(id);
    }

    public District getDistrictById(Long id) {
        return withNeighborhoodCount(districtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ilce bulunamadi")));
    }

    public List<District> getAllDistricts() {
        List<District> districts = districtRepository.findAll();
        districts.forEach(this::withNeighborhoodCount);
        return districts;
    }

    public List<District> getDistrictsByCityId(Long cityId) {
        List<District> districts = districtRepository.findByCityId(cityId);
        districts.forEach(this::withNeighborhoodCount);
        return districts;
    }

    private District withNeighborhoodCount(District district) {
        district.setNeighborhoodCount(neighborhoodRepository.countByDistrictId(district.getId()));
        return district;
    }
}
