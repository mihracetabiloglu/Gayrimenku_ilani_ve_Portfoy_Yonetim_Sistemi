package com.gayrimenkul.system.repository;

import com.gayrimenkul.system.entity.Property;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    // Kategoriye göre (Satılık/Kiralık)
    List<Property> findByCategoryId(Long categoryId);

    // Şehre göre filtrele
    List<Property> findByCityId(Long cityId);

    // Kullanıcının kendi ilanları
    List<Property> findByUserId(Long userId);

    // Gelişmiş Arama Filtresi (Örnek)
    @Query("SELECT p FROM Property p WHERE " +
           "(:cityId IS NULL OR p.city.id = :cityId) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Property> filterProperties(@Param("cityId") Long cityId, 
                                    @Param("categoryId") Long categoryId,
                                    @Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice);
}
