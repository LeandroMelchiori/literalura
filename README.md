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
## 2. Construcción

### Requisitos previos
- **Java 17** o superior.
- **Maven 3.8** o superior.
- **PostgreSQL** instalado y en ejecución.

### Configuración de la Base de Datos
1. Asegúrate de tener PostgreSQL en funcionamiento.
2. Crea una base de datos llamada `literalura` ejecutando el siguiente comando:
   ```sql
   CREATE DATABASE literalura;
3. Configura las credenciales de acceso en el archivo application.properties ubicado en src/main/resources/:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/literalura
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   spring.jpa.hibernate.ddl-auto=update
- Reemplaza tu_usuario y tu_contraseña con las credenciales de tu base de datos.

### Construcción del Proyecto
1. Clona el repositorio desde GitHub:
   ```bash
   git clone https://github.com/tu-usuario/literalura.git
   
2. Navega al directorio del proyecto:
   ```bash
   cd literalura
3. Compila y construye el proyecto utilizando Maven:
   ```bash
   mvn clean install

## Ejecución

1. Inicia la aplicación ejecutando el archivo JAR generado:
   ```bash
   java -jar target/literalura-1.0.0.jar
   
2. Sigue las instrucciones en la consola para interactuar con las funcionalidades de la aplicación.


## Ejemplo de Interacción

### Menú principal:
1. Buscar libro por título
2. Listar todos los libros
3. Listar libros por idioma
4. Listar todos los autores
5. Buscar autores vivos hasta un año específico
0. Salir

### Ejemplo: Buscar libro por título:
   ```yaml
   Ingrese el título del libro: Pride and Prejudice
   Resultado: Libro encontrado: [Título: Pride and Prejudice, Autor: Jane Austen, Idioma: Inglés]
   ```
### Listar todos los autores
   ```yaml
   Autores registrados:
   - [Nombre: Jane Austen, Año de nacimiento: 1775, Año de muerte: 1817]
   - [Nombre: George Orwell, Año de nacimiento: 1903, Año de muerte: 1950]
   ```

### Buscar autores vivos hasta un año específico
   ```yaml
   Ingrese el año: 1800
   Autores vivos hasta 1800:
   - [Nombre: Jane Austen, Año de nacimiento: 1775, Año de muerte: 1817]
   ```

## Licencia
### Este proyecto se distribuye bajo la licencia MIT. Puedes consultar el archivo LICENSE para más detalles.

## Contribuciones
### ¡Las contribuciones son bienvenidas! Si deseas colaborar:
1. Haz un fork del repositorio.
2. Crea una nueva rama (git checkout -b feature/nueva-funcionalidad).
3. Haz tus cambios y realiza un commit (git commit -m 'Agrego nueva funcionalidad').
4. Sube tus cambios (git push origin feature/nueva-funcionalidad).
5. Abre un Pull Request.
