# 📚 LiteraLura API

[![CI](https://github.com/LeandroMelchiori/literalura/actions/workflows/ci.yml/badge.svg)](https://github.com/LeandroMelchiori/literalura/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-green)
![License](https://img.shields.io/badge/license-MIT-blue)

**LiteraLura** es una **API REST** construida con Spring Boot que busca libros en el
[Proyecto Gutenberg](https://gutenberg.org/) (a través de la API pública
[Gutendex](https://gutendex.com/)), los persiste en PostgreSQL y expone consultas
sobre libros y autores. Incluye documentación interactiva con Swagger, manejo de
errores centralizado, tests automatizados, contenedorización con Docker y despliegue
continuo.

> Originalmente un desafío de consola de Alura, reconvertido en un servicio web
> desplegable y documentado.

---

## ✨ Características

- 🔎 **Búsqueda y registro** de libros por título desde Gutendex.
- 📚 **Listado** de libros, con filtro por idioma.
- 👤 **Consulta de autores**, incluyendo autores vivos en un año dado.
- 📊 **Estadísticas** agregadas de descargas (promedio, máximo, mínimo).
- 📖 **Documentación OpenAPI / Swagger UI** autogenerada.
- 🛡️ **Manejo de errores** consistente con `ProblemDetail` (RFC 7807).
- 📄 **Paginación** en los listados (`?page=&size=&sort=`).
- 🗃️ **Migraciones de esquema versionadas** con Flyway.
- ✅ **Tests** unitarios (Mockito) y de capa web (MockMvc) sobre H2.
- 🐳 **Docker + Docker Compose** listos para levantar la app con su base de datos.
- 🚀 **CI en GitHub Actions** y blueprint de despliegue para Render.

---

## 🛠️ Stack

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.4 (Web, Data JPA, Validation, Actuator) |
| Base de datos | PostgreSQL (H2 en tests) |
| Migraciones | Flyway |
| Documentación | springdoc-openapi (Swagger UI) |
| Cliente HTTP | `java.net.http.HttpClient` |
| Tests | JUnit 5, Mockito, Spring MockMvc |
| Build | Maven |
| Infra | Docker, Docker Compose, GitHub Actions, Render |

---

## 🚀 Cómo ejecutarlo

### Opción A — Docker Compose (recomendada)

Levanta la aplicación **y** la base de datos con un solo comando:

```bash
docker compose up --build
```

La API queda disponible en `http://localhost:8080`.

### Opción B — Local con Maven

Requiere una instancia de PostgreSQL. Configurá las variables de entorno (o usá los
valores por defecto `localhost:5432/literalura`, usuario/clave `postgres`):

```bash
export DB_HOST=localhost DB_PORT=5432 DB_NAME=literalura DB_USER=postgres DB_PASSWORD=postgres
./mvnw spring-boot:run
```

---

## 📡 Endpoints

Base URL: `/api`

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/books/search?title={título}` | Busca en Gutendex y registra el libro |
| `GET`  | `/api/books` | Lista paginada de libros registrados |
| `GET`  | `/api/books/language/{idioma}` | Libros por idioma (`en`, `es`, ...), paginado |
| `GET`  | `/api/books/stats` | Estadísticas agregadas de descargas |
| `GET`  | `/api/authors` | Lista todos los autores |
| `GET`  | `/api/authors/alive?year={año}` | Autores vivos hasta ese año |
| `GET`  | `/actuator/health` | Health check |

### Documentación interactiva

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

### Ejemplo

```bash
# Registrar un libro
curl -X POST "http://localhost:8080/api/books/search?title=pride%20and%20prejudice"
```

```json
{
  "title": "Pride and Prejudice",
  "author": { "name": "Austen, Jane", "birthYear": 1775, "deathYear": 1817 },
  "languages": ["en"],
  "downloadCount": 12345
}
```

---

## ✅ Tests

```bash
./mvnw verify
```

Los tests usan una base de datos H2 en memoria, por lo que no requieren PostgreSQL.

---

## ☁️ Despliegue

El repositorio incluye [`render.yaml`](render.yaml), un blueprint de
[Render](https://render.com) que aprovisiona una base de datos PostgreSQL y despliega
la API desde el `Dockerfile`. También puede desplegarse en cualquier plataforma que
soporte contenedores (Railway, Fly.io, etc.) usando las variables de entorno
`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` y `PORT`.

---

## 📂 Estructura

```
src/main/java/com/alura/literalura
├── config/       # Beans (HttpClient, OpenAPI)
├── controller/   # Controladores REST
├── dto/          # Objetos de transferencia
├── exception/    # Excepciones y handler global
├── model/        # Entidades JPA
├── repository/   # Repositorios Spring Data
└── service/      # Lógica de negocio
```

---

## 📜 Licencia

Distribuido bajo licencia MIT. Ver [LICENSE](LICENSE).
