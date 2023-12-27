package com.amigos.authenautho.demo.auth;

import com.amigos.authenautho.demo.entities.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private Role role;
}
