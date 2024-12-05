package com.proyectogrado.plataforma.ExceptionHandling;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String message) {
        super(message);
    }
}