package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.PasswordResetToken;
import com.gayrimenkul.system.entity.User;
import com.gayrimenkul.system.repository.PasswordResetTokenRepository;
import com.gayrimenkul.system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public String createPasswordResetToken(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("E-posta adresi bos olamaz.");
        }

        Optional<User> userOpt = userRepository.findByEmail(email.trim());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Bu e-posta adresiyle kayitli kullanici bulunamadi.");
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        tokenRepository.save(resetToken);
        emailService.sendPasswordResetMail(user.getEmail(), token);

        return token;
    }

    public boolean resetPassword(String token, String newPassword) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Sifre sifirlama baglantisi gecersiz.");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("Sifre bos olamaz.");
        }

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token.trim());
        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("Sifre sifirlama baglantisi gecersiz.");
        }

        PasswordResetToken resetToken = tokenOpt.get();
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Sifre sifirlama baglantisi suresi dolmus.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);

        return true;
    }
}
