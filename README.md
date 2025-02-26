# 📚 Literalura

📖 **Literalura** es una aplicación desarrollada en Java utilizando Spring Boot para gestionar libros y autores en una base de datos. Ofrece funcionalidades para buscar, listar y gestionar información sobre libros y autores de manera eficiente.

## 🚀 Características principales
- 🔍 **Búsqueda de libros**: Encuentra libros por título o autor.
- 📋 **Gestión de biblioteca**: Lista y administra libros y autores registrados.
- 📅 **Consulta de autores**: Busca autores que estuvieron vivos en un año específico.
- 🌐 **Filtrado por idioma**: Lista libros según su idioma.
- 📈 **Estadísticas**: Muestra las estadísticas generales de la base de datos.

## 🛠️ Tecnologías utilizadas
- **Java 17**
- **Spring Boot 3**
- **PostgreSQL**
- **Maven**
- **Gutendex API**
- **Intellij**

## 🏗️ Construcción del Proyecto
🔹 1. Clona el repositorio desde GitHub:
   ```bash
   git clone https://github.com/tu-usuario/literalura.git
```
🔹 2. Configurar la Base de Datos
- Crea una base de datos llamada `literalura`.
- Configura las credenciales en el archivo `application.properties`.
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/literalura
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
```
💡 - Reemplaza tu_usuario y tu_contraseña con las credenciales de tu base de datos.
   

## 🎮 Uso de la aplicación

### 📌 Abrir el proyecto en IntelliJ IDEA

🔹 1. Abre IntelliJ IDEA.

🔹 2. Selecciona Open y elige la carpeta raíz del proyecto donde está el archivo pom.xml.

🔹 3. IntelliJ configurará automáticamente el proyecto como un proyecto Maven.

🔹 4. Navega hasta el archivo LiteraluraApplication.java en el paquete com.alura.literalura .

🔹 5. Haz clic derecho en el archivo y selecciona Run 'LiteraluraApplication.main()'.

🔹 6. La aplicación se ejecutará en la consola integrada de IntelliJ IDEA, mostrando el menú principal.
   

## 🚀 Ejemplo de interaccion

### 💡 Al ejecutar la aplicación, verás un menú en la consola con las siguientes opciones:


### 🔹 Menú principal:
```yaml
1. Buscar libro por título
2. Listar todos los libros
3. Listar libros por idioma
4. Listar todos los autores
5. Buscar autores vivos hasta un año específico
0. Salir
```

### 🔹 Buscar libro por título:
   ```yaml
   Ingrese el título del libro: Pride and Prejudice
   Resultado: Libro encontrado: [Título: Pride and Prejudice, Autor: Jane Austen, Idioma: Inglés]
   ```
### 🔹 Listar todos los autores
   ```yaml
   Autores registrados:
   - [Nombre: Jane Austen, Año de nacimiento: 1775, Año de muerte: 1817]
   - [Nombre: George Orwell, Año de nacimiento: 1903, Año de muerte: 1950]
   ```

### 🔹 Buscar autores vivos hasta un año específico
   ```yaml
   Ingrese el año: 1800
   Autores vivos hasta 1800:
   - [Nombre: Jane Austen, Año de nacimiento: 1775, Año de muerte: 1817]
   ```

## 📜 Licencia
#### 📌 Este proyecto se distribuye bajo la licencia MIT. Puedes consultar el archivo LICENSE para más detalles.
