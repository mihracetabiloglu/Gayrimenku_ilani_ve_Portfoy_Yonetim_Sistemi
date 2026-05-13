package com.gayrimenkul.system.controller;

import com.gayrimenkul.system.entity.Favorite;
import com.gayrimenkul.system.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Favorite> addFavorite(@RequestBody Favorite favorite) {
        Favorite savedFavorite = favoriteService.addFavorite(favorite);
        return new ResponseEntity<>(savedFavorite, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Favorite>> getFavoritesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavoritesByUserId(userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long id) {
        favoriteService.removeFavorite(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-user-property")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFavoriteByUserAndProperty(
            @RequestParam Long userId,
            @RequestParam Long propertyId) {
        favoriteService.removeFavoriteByUserAndProperty(userId, propertyId);
        return ResponseEntity.noContent().build();
    }
}
