package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.Favorite;
import com.gayrimenkul.system.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    // Favoriye Ekle
    @Transactional
    public Favorite addFavorite(Favorite favorite) {
        // Mükerrer kayıt kontrolü
        boolean exists = favoriteRepository
            .findByUserIdAndPropertyId(favorite.getUser().getId(), favorite.getProperty().getId())
            .isPresent();

        if (exists) {
            throw new RuntimeException("Bu ilan zaten favorilerinizde mevcut.");
        }
        
        return favoriteRepository.save(favorite);
    }

    // Kullanıcının Favorilerini Listele
    public List<Favorite> getFavoritesByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    // Favoriden Kaldır (ID ile)
    @Transactional
    public void removeFavorite(Long id) {
        if (!favoriteRepository.existsById(id)) {
            throw new RuntimeException("Favori kaydı bulunamadı.");
        }
        favoriteRepository.deleteById(id);
    }

    // Favoriden Kaldır (Kullanıcı ve İlan ID ile - Daha kullanışlıdır)
    @Transactional
    public void removeFavoriteByUserAndProperty(Long userId, Long propertyId) {
        favoriteRepository.deleteByUserIdAndPropertyId(userId, propertyId);
    }
}