package com.example.backend.exception;


public class UsuarioAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UsuarioAlreadyExistsException(String message) {
        super(message);
    }
}

