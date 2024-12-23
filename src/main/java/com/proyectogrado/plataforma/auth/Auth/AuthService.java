package com.proyectogrado.plataforma.auth.Auth;

import com.proyectogrado.plataforma.auth.Jwt.JwtService;
import com.proyectogrado.plataforma.auth.Entities.User;
import com.proyectogrado.plataforma.auth.Entities.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        try {
            // Attempt to authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Fetch the user from the database
            UserDetails user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found."));

            // Generate and return the token
            String token = jwtService.getToken(user);

            return AuthResponse.builder()
                    .token(token)
                    .message("") // No message in successful login
                    .build();

        } catch (AuthenticationException e) {
            return AuthResponse.builder()
                    .token("") // Empty token since authentication failed
                    .message("Invalid username or password.")
                    .build();
        }
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return AuthResponse.builder()
                    .token("") // Empty token since registration failed
                    .message("Email (username) already taken. Please choose a different one.")
                    .build();
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .role(request.getRole())
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .message("") // No message in successful registration
                .build();
    }
}
