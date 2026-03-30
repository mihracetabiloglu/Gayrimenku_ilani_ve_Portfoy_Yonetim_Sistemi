package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.City;
import com.gayrimenkul.system.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    // Tüm şehirleri listele (İlçeler Lazy loading ise burada dikkat edilmeli)
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    // ID ile şehir bul
    public City getCityById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Şehir bulunamadı. ID: " + id));
    }

    // Yeni şehir ekle veya güncelle
    @Transactional
    public City saveCity(City city) {
        // Eğer iş kuralları gereği aynı isimli şehir olmamalıysa kontrol eklenebilir
        return cityRepository.save(city);
    }

    // Şehri sil (CascadeType.ALL olduğu için ilçeleri de silinir!)
    @Transactional
    public void deleteCity(Long id) {
        if (!cityRepository.existsById(id)) {
            throw new RuntimeException("Silinmek istenen şehir bulunamadı.");
        }
        cityRepository.deleteById(id);
    }
}