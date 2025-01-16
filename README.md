# Literalura

Literalura es una aplicación desarrollada en Java utilizando Spring Boot para gestionar libros y autores en una base de datos. Ofrece funcionalidades para buscar, listar y gestionar información sobre libros y autores de manera eficiente.

## Funcionalidades

### Libros
- Buscar libros por título.
- Listar todos los libros.
- Listar libros por idioma.

### Autores
- Listar todos los autores registrados.
- Buscar autores vivos hasta un año específico.

## Tecnologías utilizadas
- **Java 17**
- **Spring Boot** (v3.4.1)
- **Spring Data JPA**: Para el manejo de la persistencia.
- **PostgreSQL**: Base de datos relacional.
- **Hibernate ORM**: Framework de mapeo objeto-relacional.
- **Jakarta Persistence API**: Para definir las entidades.
- **Maven**: Como herramienta de construcción y gestión de dependencias.

## Estructura del proyecto

El proyecto sigue la estructura estándar de un proyecto Spring Boot:
src/ ├── main/ │ ├── java/ │ │ ├── com.alura.literalura/ │ │ │ ├── model/ # Clases de entidad (Book, Author). │ │ │ ├── dto/ # Clases de transferencia de datos. │ │ │ ├── repository/ # Interfaces JPA para acceso a la base de datos. │ │ │ ├── service/ # Lógica de negocio (BookService, AuthorService). │ │ │ ├── Main.java # Punto de entrada de la aplicación. │ ├── resources/ │ ├── application.properties # Configuraciones de la base de datos.


## Configuración del proyecto

1. **Base de datos**:
   - Asegúrate de tener PostgreSQL instalado y configurado.
   - Crea una base de datos llamada `literalura`.
   - Configura las credenciales en el archivo `application.properties`.

   Ejemplo:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/literalura
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña

## Construcción y Ejecución

### 1. Configurar la Base de Datos
- Asegúrate de tener **PostgreSQL** instalado y en funcionamiento.
- Crea una base de datos llamada `literalura`.
- Configura las credenciales en el archivo `application.properties`.

Ejemplo de configuración en `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/literalura
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
```

# Ejemplo de uso
## Menú principal:
1. Buscar libro por título
2. Listar todos los libros
3. Listar libros por idioma
4. Listar todos los autores
5. Buscar autores vivos hasta un año específico
0. Salir

# Buscar libro por título:
Ingrese el título del libro: Pride and Prejudice
Resultado: Libro encontrado: [Título: Pride and Prejudice, Autor: Jane Austen, Idioma: Inglés]

