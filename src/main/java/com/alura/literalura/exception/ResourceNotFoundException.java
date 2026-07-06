package com.alura.literalura.exception;

/** El recurso solicitado (ejemplar, socio, préstamo...) no existe. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " no encontrado con id: " + id);
    }
}
