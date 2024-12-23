package com.proyectogrado.plataforma.auth.ExceptionHandling;

public class UsernameAlreadyTakenException extends RuntimeException {
    public UsernameAlreadyTakenException(String message) {
        super(message);
    }
}
