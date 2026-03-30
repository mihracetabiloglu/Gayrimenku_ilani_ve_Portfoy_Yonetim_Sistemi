package com.gayrimenkul.system.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gayrimenkul.system.entity.District;
import com.gayrimenkul.system.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Genel olarak sadece okuma yapıldığını belirtir
public class DistrictService {
    
    private final DistrictRepository districtRepository;

    @Transactional // Yazma işlemi olduğu için readOnly'yi ezer
    public District addDistrict(District district) {
        return districtRepository.save(district);
    }

    @Transactional
    public District updateDistrict(Long id, District updatedDistrict) {        
        return districtRepository.findById(id)
                .map(district -> {
                    district.setName(updatedDistrict.getName());
                    district.setCity(updatedDistrict.getCity());
                    return districtRepository.save(district);
                })
                .orElseThrow(() -> new RuntimeException("İlçe bulunamadı"));
    }

    @Transactional
    public void deleteDistrict(Long id) {
        if (!districtRepository.existsById(id)) {
            throw new RuntimeException("İlçe bulunamadı");
        }
        districtRepository.deleteById(id);
    }

    public District getDistrictById(Long id) {
        return districtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İlçe bulunamadı"));
    }

    // Şehir bazlı listeleme
    public List<District> getDistrictsByCityId(Long cityId) {
        return districtRepository.findByCityId(cityId);
    } 
}
