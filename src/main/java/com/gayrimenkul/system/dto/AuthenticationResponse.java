package com.gayrimenkul.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private Long expiresAt;
    private Boolean rememberMe;
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private java.util.List<String> roles;
}
