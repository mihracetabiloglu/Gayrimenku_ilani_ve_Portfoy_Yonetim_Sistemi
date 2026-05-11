package com.gayrimenkul.system.dto;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String fullName;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String role;
    private Boolean wantsToPostListing;
}
