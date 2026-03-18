package com.main.tutorly.dto;

import com.main.tutorly.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private User.Role role;
}
