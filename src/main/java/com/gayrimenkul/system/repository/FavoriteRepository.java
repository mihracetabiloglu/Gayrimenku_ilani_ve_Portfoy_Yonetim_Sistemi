package com.gayrimenkul.system.repository;

import com.gayrimenkul.system.entity.Favorite;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    // Bir kullanıcının tüm favorilerini getir
    List<Favorite> findByUserId(Long userId);

    // Belirli bir kullanıcı belirli bir ilanı favoriye eklemiş mi kontrolü
    Optional<Favorite> findByUserIdAndPropertyId(Long userId, Long propertyId);

    // Favoriden çıkarma işlemi için kolaylık sağlar
    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);
}
