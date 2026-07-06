package com.alura.literalura.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String title) {
        super("No se encontró ningún libro con el título: " + title);
    }
}
