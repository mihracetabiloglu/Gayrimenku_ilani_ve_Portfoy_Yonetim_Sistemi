package com.gayrimenkul.system.controller;

import com.gayrimenkul.system.dto.AuthenticationRequest;
import com.gayrimenkul.system.dto.AuthenticationResponse;
import com.gayrimenkul.system.dto.RegistrationRequest;
import com.gayrimenkul.system.entity.User;
import com.gayrimenkul.system.security.JwtUtil;
import com.gayrimenkul.system.service.SystemLogService;
import com.gayrimenkul.system.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final SystemLogService systemLogService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request, HttpServletRequest httpRequest) {
        User user = null;
        try {
            user = userService.getUserByUsernameOrEmail(request.getUsername());
            if (Boolean.FALSE.equals(user.getActive())) {
                logAuthEvent(user.getId(), "LOGIN_FAIL", "Pasif kullanici giris denemesi: " + user.getUsername(), httpRequest);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Kullanici hesabi pasif.");
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails, request.isRememberMe());

            AuthenticationResponse response = AuthenticationResponse.builder()
                    .token(token)
                    .expiresAt(jwtUtil.extractExpiration(token).getTime())
                    .rememberMe(request.isRememberMe())
                    .userId(user.getId())
                    .username(userDetails.getUsername())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .roles(userDetails.getAuthorities()
                            .stream()
                            .map(auth -> auth.getAuthority())
                            .collect(Collectors.toList()))
                    .build();

            logAuthEvent(user.getId(), "LOGIN_SUCCESS", "Kullanici giris yapti: " + user.getUsername(), httpRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            logAuthEvent(user == null ? null : user.getId(), "LOGIN_FAIL", "Hatali sifre ile giris denemesi: " + request.getUsername(), httpRequest);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Hatali kullanici adi veya sifre");
        } catch (RuntimeException e) {
            logAuthEvent(user == null ? null : user.getId(), "LOGIN_FAIL", "Basarisiz giris denemesi: " + request.getUsername(), httpRequest);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Hatali kullanici adi veya sifre");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request, HttpServletRequest httpRequest) {
        try {
            if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Sifre ve sifre tekrar eslesmiyor.");
            }
            User user = new User();
            user.setFullName(request.getFullName());
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setActive(true);
            User savedUser = userService.createUserForRegistration(user, Boolean.TRUE.equals(request.getWantsToPostListing()));
            logAuthEvent(savedUser.getId(), "REGISTER_SUCCESS", "Yeni kullanici kaydi: " + savedUser.getUsername(), httpRequest);
            return new ResponseEntity<>("Kayit basarili", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            logAuthEvent(null, "REGISTER_FAIL", "Basarisiz kayit denemesi: " + request.getUsername() + " - " + e.getMessage(), httpRequest);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return ResponseEntity.ok(jwtUtil.validateToken(token));
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    private void logAuthEvent(Long userId, String action, String details, HttpServletRequest request) {
        try {
            systemLogService.createLog(userId, action, details, resolveClientIp(request));
        } catch (Exception ignored) {
            // Loglama hatasi kullanicinin auth akisina engel olmamali.
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
