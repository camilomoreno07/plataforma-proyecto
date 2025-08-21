package com.proyectogrado.plataforma.service;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.auth.Auth.AuthResponse;
import com.proyectogrado.plataforma.auth.Auth.AuthService;
import com.proyectogrado.plataforma.auth.Auth.LoginRequest;
import com.proyectogrado.plataforma.auth.Auth.RegisterRequest;
import com.proyectogrado.plataforma.auth.Entities.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(EmbedMongoConfig.class)
class AuthServiceTest
{
    @Autowired
    private AuthService authService;

    @Test
    void successfulLogin()
    {
        LoginRequest loginRequest = new LoginRequest("student1@gmail.com", "1234");
        AuthResponse authResponse = authService.login(loginRequest);

        assertNotEquals("", authResponse.getToken());
        assertNotEquals("Invalid username or password.", authResponse.getMessage());
        assertNotEquals("", authResponse.getRole());
    }

    @Test
    void unsuccessfulLogin()
    {
        LoginRequest loginRequest = new LoginRequest("unknown@gmail.com", "unknown");
        AuthResponse authResponse = authService.login(loginRequest);

        assertEquals("", authResponse.getToken());
        assertEquals("Invalid username or password.", authResponse.getMessage());
        assertEquals("", authResponse.getRole());
    }

    @Test
    void register()
    {
        RegisterRequest registerRequest = new RegisterRequest("newcomer@gmail.com", "1234", "The", "Newcomer", "Colombia", Role.STUDENT);
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("newcomer@gmail.com", "1234");
        AuthResponse authResponse = authService.login(loginRequest);

        assertNotEquals("", authResponse.getToken());
        assertNotEquals("Invalid username or password.", authResponse.getMessage());
        assertNotEquals("", authResponse.getRole());
    }
}