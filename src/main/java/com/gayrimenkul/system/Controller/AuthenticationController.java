package com.gayrimenkul.system.Controller;

import com.gayrimenkul.system.dto.AuthenticationRequest;
import com.gayrimenkul.system.dto.AuthenticationResponse;
import com.gayrimenkul.system.security.JwtUtil;
import com.gayrimenkul.system.service.CustomUserDetailsService;

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
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            AuthenticationResponse response = AuthenticationResponse.builder()
                    .token(token)
                    .username(userDetails.getUsername())
                    .roles(userDetails.getAuthorities()
                            .stream()
                            .map(auth -> auth.getAuthority())
                            .collect(Collectors.toList()))
                    .build();

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Hatalı kullanıcı adı veya şifre");
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
