package com.gayrimenkul.system.Controller;

import com.gayrimenkul.system.dto.AuthenticationRequest;
import com.gayrimenkul.system.dto.AuthenticationResponse;
import com.gayrimenkul.system.dto.RegistrationRequest;
import com.gayrimenkul.system.entity.User;
import com.gayrimenkul.system.security.JwtUtil;
import com.gayrimenkul.system.service.UserService;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            User user = userService.getUserByUsernameOrEmail(request.getUsername());
            if (Boolean.FALSE.equals(user.getActive())) {
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

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Hatali kullanici adi veya sifre");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Hatali kullanici adi veya sifre");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
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
            userService.createUserForRegistration(user, Boolean.TRUE.equals(request.getWantsToPostListing()));
            return new ResponseEntity<>("Kayit basarili", HttpStatus.CREATED);
        } catch (RuntimeException e) {
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
}
