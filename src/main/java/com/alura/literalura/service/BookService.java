package com.alura.literalura.service;

import com.alura.literalura.dto.ApiResponse;
import com.alura.literalura.dto.AuthorDTO;
import com.alura.literalura.dto.BookDTO;
import com.alura.literalura.model.Author;
import com.alura.literalura.model.Book;
import com.alura.literalura.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepository repository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private ConsumoAPI consumoAPI;

    @Transactional
    public List<BookDTO> obtenerTodosLosLibros() {
        List<Book> libros = repository.findAll(); // Carga libros
        return libros.stream()
                .map(this::toDto) // Convierte a DTO (si accede a relaciones lazy, la sesión estará activa)
                .collect(Collectors.toList());
    }


    public List<BookDTO> obtenerLibrosPorIdioma(String idioma) {
        return convierteDatos(repository.findByLanguage(idioma));
    }

    public BookDTO buscarLibroPorTitulo(String titulo) {
        String url = "https://gutendex.com/books/?search=" + titulo.replace(" ", "+");
        String jsonResponse = consumoAPI.obtenerDatos(url);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ApiResponse response = objectMapper.readValue(jsonResponse, ApiResponse.class);

            if (response.getResults().isEmpty()) {
                System.out.println("No se encontraron libros con el título proporcionado.");
                return null;
            }

            BookDTO bookDTO = getBookDTO(response);

            // Crear o buscar autor
            Author author = new Author(
                    bookDTO.author().name(),
                    bookDTO.author().birth_year(),
                    bookDTO.author().death_year()
            );
            author = authorService.guardarAutor(author); // Obtiene una entidad gestionada

            // Verificar si el libro ya existe
            if (repository.findByTitle(bookDTO.title()).isPresent()) {
                System.out.println("El libro ya está registrado.");
                return bookDTO; // Retorna el libro existente
            }

            // Crear y guardar el libro
            Book book = toEntity(bookDTO);
            book.setAuthor(author); // Asocia el autor al libro
            repository.save(book);

            return bookDTO;
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la respuesta JSON: " + e.getMessage(), e);
        }
    }


    private static BookDTO getBookDTO(ApiResponse response) {
        var result = response.getResults().get(0);

        // Seleccionar el primer autor, o un autor predeterminado si no hay ninguno
        AuthorDTO author = result.getAuthors() != null && !result.getAuthors().isEmpty()
                ? new AuthorDTO(
                result.getAuthors().get(0).getName(),
                result.getAuthors().get(0).getBirthYear(),
                result.getAuthors().get(0).getDeathYear()
        )
                : new AuthorDTO("Desconocido", null, null);

        return new BookDTO(
                result.getTitle(),
                author,
                result.getLanguages(),
                result.getDownloadCount()
        );
    }

    private List<BookDTO> convierteDatos(List<Book> books) {
        return books.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private BookDTO toDto(Book book) {
        Author author = book.getAuthor(); // Obtenemos el autor del modelo Book

        // Convertimos el Author a AuthorDTO
        AuthorDTO authorDTO = new AuthorDTO(
                author.getName(),
                author.getBirthYear(),
                author.getDeathYear()
        );

        // Creamos el BookDTO usando el AuthorDTO
        return new BookDTO(
                book.getTitle(),
                authorDTO,
                List.of(book.getLanguage()), // Convertimos el idioma a una lista
                book.getDownloadCount()
        );
    }

    private Book toEntity(BookDTO bookDTO) {
        Author author = new Author(
                bookDTO.author().name(),
                bookDTO.author().birth_year(),
                bookDTO.author().death_year()
        );

        String language = bookDTO.languages() != null && !bookDTO.languages().isEmpty()
                ? bookDTO.languages().get(0)
                : "Desconocido";

        return new Book(
                bookDTO.title(),
                author,
                language,
                bookDTO.downloadCount()
        );
    }

}
