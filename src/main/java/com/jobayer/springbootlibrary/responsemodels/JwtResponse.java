package com.jobayer.springbootlibrary.responsemodels;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String email;
    private String firstName;
    private String lastName;
    private String role;

    public JwtResponse(String token, String email, String firstName, String lastName, String role) {
        this.token = token;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
} 