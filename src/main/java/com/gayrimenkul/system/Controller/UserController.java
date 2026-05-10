package com.gayrimenkul.system.Controller;

import com.gayrimenkul.system.entity.User;
import com.gayrimenkul.system.service.UserService;
import com.gayrimenkul.system.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @Autowired
    private PasswordResetService passwordResetService;
    // Şifre sıfırlama isteği (token üretir ve "mail" loglar)
    @PostMapping("/password-reset-request")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        String token = passwordResetService.createPasswordResetToken(email);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kullanıcı bulunamadı");
        }
        // Gerçek projede burada mail gönderimi yapılır. Şimdilik log veya response ile gösteriyoruz.
        return ResponseEntity.ok("Şifre sıfırlama linkiniz: /api/users/password-reset?token=" + token);
    }

    // Şifre sıfırlama (token ve yeni şifre ile)
    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        boolean result = passwordResetService.resetPassword(token, newPassword);
        if (result) {
            return ResponseEntity.ok("Şifre başarıyla güncellendi.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token geçersiz veya süresi dolmuş.");
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/by-username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
