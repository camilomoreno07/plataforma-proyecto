package com.proyectogrado.plataforma.auth.ExceptionHandling;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String message) {
        super(message);
    }
}