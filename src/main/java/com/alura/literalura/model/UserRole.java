package com.alura.literalura.model;

/**
 * Rol del usuario:
 * ADMIN gestiona usuarios; LIBRARIAN opera la biblioteca; CLIENTE es un socio
 * con acceso a su propio portal (sus préstamos y reservas).
 */
public enum UserRole {
    ADMIN,
    LIBRARIAN,
    CLIENTE
}
