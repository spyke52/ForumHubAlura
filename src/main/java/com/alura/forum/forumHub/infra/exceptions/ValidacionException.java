package com.alura.forum.forumHub.infra.exceptions;

public class ValidacionException extends RuntimeException {
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}