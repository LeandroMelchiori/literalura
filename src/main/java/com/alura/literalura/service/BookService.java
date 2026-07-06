package com.alura.literalura.service;

import com.alura.literalura.dto.AuthorDTO;
import com.alura.literalura.dto.BookDTO;
import com.alura.literalura.dto.StatsDTO;
import com.alura.literalura.dto.gutendex.GutendexAuthor;
import com.alura.literalura.dto.gutendex.GutendexBook;
import com.alura.literalura.dto.gutendex.GutendexResponse;
import com.alura.literalura.exception.BookNotFoundException;
import com.alura.literalura.exception.ExternalApiException;
import com.alura.literalura.model.Author;
import com.alura.literalura.model.Book;
import com.alura.literalura.repository.BookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private static final String GUTENDEX_URL = "https://gutendex.com/books/?search=";

    private final BookRepository repository;
    private final AuthorService authorService;
    private final ConsumoAPI consumoAPI;
    private final ObjectMapper objectMapper;

    public BookService(BookRepository repository,
                       AuthorService authorService,
                       ConsumoAPI consumoAPI,
                       ObjectMapper objectMapper) {
        this.repository = repository;
        this.authorService = authorService;
        this.consumoAPI = consumoAPI;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public Page<BookDTO> obtenerTodosLosLibros(Pageable pageable) {
        return repository.findAllWithAuthor(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<BookDTO> obtenerLibrosPorIdioma(String idioma, Pageable pageable) {
        return repository.findByLanguage(idioma, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public StatsDTO obtenerEstadisticas() {
        List<Book> libros = repository.findAll();
        var stats = libros.stream()
                .mapToInt(Book::getDownloadCount)
                .summaryStatistics();
        return new StatsDTO(
                libros.size(),
                authorService.contarAutores(),
                libros.isEmpty() ? 0.0 : stats.getAverage(),
                libros.isEmpty() ? 0 : stats.getMax(),
                libros.isEmpty() ? 0 : stats.getMin());
    }

    /**
     * Busca un libro en Gutendex por título. Si lo encuentra, lo persiste
     * (junto a su autor) evitando duplicados y devuelve su representación.
     */
    @Transactional
    public BookDTO buscarYRegistrarLibro(String titulo) {
        String url = GUTENDEX_URL + titulo.replace(" ", "+");
        GutendexResponse response = deserializar(consumoAPI.obtenerDatos(url));

        if (response.results() == null || response.results().isEmpty()) {
            throw new BookNotFoundException(titulo);
        }

        BookDTO bookDTO = toBookDTO(response.results().get(0));

        return repository.findByTitle(bookDTO.title())
                .map(this::toDto)
                .orElseGet(() -> {
                    Author author = authorService.guardarAutor(new Author(
                            bookDTO.author().name(),
                            bookDTO.author().birthYear(),
                            bookDTO.author().deathYear()));
                    Book book = toEntity(bookDTO);
                    book.setAuthor(author);
                    return toDto(repository.save(book));
                });
    }

    private GutendexResponse deserializar(String json) {
        try {
            return objectMapper.readValue(json, GutendexResponse.class);
        } catch (JsonProcessingException e) {
            throw new ExternalApiException("No se pudo interpretar la respuesta de Gutendex.", e);
        }
    }

    private BookDTO toBookDTO(GutendexBook book) {
        GutendexAuthor first = (book.authors() != null && !book.authors().isEmpty())
                ? book.authors().get(0)
                : null;
        AuthorDTO author = (first != null)
                ? new AuthorDTO(first.name(), first.birthYear(), first.deathYear())
                : new AuthorDTO("Desconocido", null, null);

        int downloads = book.downloadCount() != null ? book.downloadCount() : 0;
        return new BookDTO(book.title(), author, book.languages(), downloads);
    }

    private BookDTO toDto(Book book) {
        Author author = book.getAuthor();
        AuthorDTO authorDTO = new AuthorDTO(
                author.getName(),
                author.getBirthYear(),
                author.getDeathYear());
        return new BookDTO(
                book.getTitle(),
                authorDTO,
                List.of(book.getLanguage()),
                book.getDownloadCount());
    }

    private Book toEntity(BookDTO bookDTO) {
        String language = (bookDTO.languages() != null && !bookDTO.languages().isEmpty())
                ? bookDTO.languages().get(0)
                : "Desconocido";
        return new Book(bookDTO.title(), null, language, bookDTO.downloadCount());
    }
}
