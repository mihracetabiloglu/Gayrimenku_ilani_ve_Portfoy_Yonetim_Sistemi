package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.Neighborhood;
import com.gayrimenkul.system.repository.NeighborhoodRepository;
import com.gayrimenkul.system.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NeighborhoodService {
    private final NeighborhoodRepository neighborhoodRepository;
    private final PropertyRepository propertyRepository;

    public NeighborhoodService(NeighborhoodRepository neighborhoodRepository, PropertyRepository propertyRepository) {
        this.neighborhoodRepository = neighborhoodRepository;
        this.propertyRepository = propertyRepository;
    }

    public List<Neighborhood> getAllNeighborhoods() {
        return neighborhoodRepository.findAll();
    }

    public Neighborhood getNeighborhoodById(Long id) {
        return neighborhoodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mahalle bulunamadi. ID: " + id));
    }

    public List<Neighborhood> getNeighborhoodsByDistrictId(Long districtId) {
        return neighborhoodRepository.findByDistrictId(districtId);
    }

    @Transactional
    public Neighborhood saveNeighborhood(Neighborhood neighborhood) {
        return neighborhoodRepository.save(neighborhood);
    }

    @Transactional
    public Neighborhood updateNeighborhood(Long id, Neighborhood details) {
        Neighborhood existing = getNeighborhoodById(id);
        existing.setName(details.getName());
        existing.setDistrict(details.getDistrict());
        if (details.getActive() != null) {
            existing.setActive(details.getActive());
        }
        return neighborhoodRepository.save(existing);
    }

    @Transactional
    public void deleteNeighborhood(Long id) {
        if (!neighborhoodRepository.existsById(id)) {
            throw new RuntimeException("Silinmek istenen mahalle bulunamadi.");
        }
        if (propertyRepository.existsByNeighborhoodId(id)) {
            throw new RuntimeException("Bu mahalleye bagli ilan oldugu icin silinemez.");
        }
        neighborhoodRepository.deleteById(id);
    }
}
