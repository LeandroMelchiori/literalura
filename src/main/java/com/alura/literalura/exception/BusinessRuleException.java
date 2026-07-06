package com.alura.literalura.exception;

/** Violación de una regla de negocio (ej: prestar un ejemplar no disponible). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
