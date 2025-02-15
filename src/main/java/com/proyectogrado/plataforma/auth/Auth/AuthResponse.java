package com.proyectogrado.plataforma.auth.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;   // Contains the JWT token if successful
    private String message; // Contains an error message if there’s an issue
    private String role; // Nuevo campo para el rol
}
