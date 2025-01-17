package com.alura.literalura;

import com.alura.literalura.service.BookService;
import com.alura.literalura.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class Main implements CommandLineRunner {
    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;
        do {
            System.out.println("1. Buscar libro por título");
            System.out.println("2. Listar todos los libros");
            System.out.println("3. Listar libros por idioma");
            System.out.println("4. Listar todos los autores");
            System.out.println("5. Listar autores vivos hasta un año específico");
            System.out.println("0. Salir");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> {
                    System.out.print("Ingrese el título del libro: ");
                    String titulo = scanner.nextLine();
                    System.out.println(bookService.buscarLibroPorTitulo(titulo));
                }
                case 2 -> bookService.obtenerTodosLosLibros().forEach(System.out::println);
                case 3 -> {
                    System.out.print("Ingrese el idioma: ");
                    String idioma = scanner.nextLine();
                    bookService.obtenerLibrosPorIdioma(idioma).forEach(System.out::println);
                }
                case 4 -> {
                    System.out.println("Autores registrados:");
                    authorService.obtenerTodosLosAutores().forEach(System.out::println);
                }
                case 5 -> {
                    System.out.print("Ingrese el año: ");
                    int year = scanner.nextInt();
                    System.out.println("Autores vivos hasta el año " + year + ":");
                    authorService.obtenerAutoresVivosHasta(year).forEach(System.out::println);
                }
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }
}
