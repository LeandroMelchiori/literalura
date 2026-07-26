<div align="center">

# 📚 LiteraLura

### Sistema full stack de gestión de biblioteca

[![CI](https://github.com/LeandroMelchiori/literalura/actions/workflows/ci.yml/badge.svg)](https://github.com/LeandroMelchiori/literalura/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F?logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=061A23)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue)

</div>

LiteraLura nació como un desafío de consola para consultar libros del Proyecto Gutenberg y evolucionó hasta convertirse en un **sistema full stack de gestión bibliotecaria**.

El proyecto combina una API REST desarrollada con Spring Boot y una SPA en React. Permite catalogar obras desde Gutendex, administrar ejemplares físicos, socios, préstamos, reservas y multas, con autenticación JWT y experiencias diferenciadas para administradores, bibliotecarios y socios.

---

## ✨ Funcionalidades

### Catálogo público

- Consulta paginada de títulos y autores.
- Búsqueda de títulos catalogados por texto.
- Importación de libros desde la API pública de Gutendex.
- Estadísticas agregadas del catálogo.
- Consulta de autores vivos en un año determinado.
- Visualización pública sin necesidad de iniciar sesión.

### Administración de la biblioteca

- Registro y gestión de ejemplares físicos mediante código de inventario.
- Alta y actualización de socios.
- Activación y suspensión de socios.
- Registro de préstamos y devoluciones.
- Detección de préstamos vencidos.
- Gestión de reservas pendientes.
- Registro y pago de multas.
- Administración de usuarios internos.

### Portal del socio

- Reserva de títulos desde el catálogo.
- Consulta de préstamos propios.
- Renovación de préstamos cuando las reglas de negocio lo permiten.
- Consulta y cancelación de reservas propias.
- Consulta de multas propias.
- Resumen inicial con préstamos activos, vencimientos próximos y multas pendientes.

### Experiencia de usuario

- Navegación adaptada al rol contenido en el JWT.
- Búsqueda con debounce en catálogo y socios.
- Formularios con selectores para evitar depender de identificadores manuales.
- Estados de carga mediante skeletons.
- Estados vacíos y mensajes de error accionables.
- Diálogos de confirmación para acciones sensibles.
- Notificaciones tipo toast.
- Fechas y estados presentados en un formato comprensible para el usuario.
- Navegación y diálogos utilizables mediante teclado.

---

## 🔐 Roles y permisos

| Rol | Capacidades principales |
|---|---|
| `ADMIN` | Gestiona usuarios internos y dispone de todas las operaciones bibliotecarias. |
| `LIBRARIAN` | Administra catálogo, ejemplares, socios, préstamos, reservas y multas. |
| `CLIENTE` | Consulta el catálogo y gestiona únicamente sus préstamos, reservas y multas. |

La autorización se aplica tanto en el backend como en el frontend. Los recursos personales utilizan controles a nivel de fila para evitar que un socio consulte o modifique información perteneciente a otro usuario.

---

## 📏 Reglas de negocio destacadas

- Solo puede prestarse un ejemplar disponible.
- El socio debe encontrarse activo.
- Un socio no puede solicitar nuevos préstamos con vencimientos o multas impagas.
- Se limita la cantidad de préstamos activos por socio.
- Una reserva puede cumplirse asignando un ejemplar disponible del título solicitado.
- Los préstamos pueden renovarse un número limitado de veces.
- No se permite renovar un préstamo vencido o un título reservado por otra persona.
- Las devoluciones fuera de término generan una multa.
- Los emails, documentos, usernames y códigos de inventario se validan para evitar duplicados.

---

## 🏗️ Arquitectura

```text
┌────────────────────────────────────────────────────────────┐
│                    Frontend React + Vite                    │
│ catálogo · login · personal · portal del socio · feedback  │
└───────────────────────────┬────────────────────────────────┘
                            │ HTTP / JSON + JWT
┌───────────────────────────▼────────────────────────────────┐
│                API REST Spring Boot 3.4                    │
│ controllers · services · repositories · reglas de negocio │
│ Spring Security · ProblemDetail · OpenAPI · Actuator       │
└───────────────────────────┬────────────────────────────────┘
                            │ JPA / Flyway
┌───────────────────────────▼────────────────────────────────┐
│                         PostgreSQL                         │
└────────────────────────────────────────────────────────────┘

              Gutendex ──► importación de libros
```

### Backend

- Java 17 y Spring Boot 3.4.
- Spring Web, Data JPA, Validation y Actuator.
- Spring Security con JWT y BCrypt.
- PostgreSQL y migraciones versionadas con Flyway.
- Errores HTTP normalizados mediante `ProblemDetail`.
- Documentación OpenAPI y Swagger UI.
- Tests con JUnit, Mockito, MockMvc, Spring Security Test y H2.

### Frontend

- React 18 y React Router.
- Vite 6.
- Context API para autenticación y notificaciones.
- Rutas protegidas y menús por rol.
- Vitest, Testing Library y jsdom.
- Cliente HTTP centralizado para consumir la API.

---

## 📂 Estructura principal

```text
literalura/
├── src/main/java/              # Backend Spring Boot
├── src/main/resources/
│   └── db/migration/           # Migraciones Flyway
├── src/test/                   # Tests backend
├── frontend/
│   ├── src/
│   │   ├── api/                # Clientes de la API
│   │   ├── components/         # Componentes reutilizables
│   │   ├── context/            # Autenticación y toasts
│   │   ├── hooks/              # Estado paginado y utilidades
│   │   └── pages/              # Pantallas por módulo y rol
│   └── package.json
├── docs/DEPLOY.md
├── docker-compose.yml
├── Dockerfile
├── render.yaml
└── pom.xml
```

---

## 🚀 Ejecución local

### Requisitos

- Java 17.
- Node.js 20 o superior.
- Docker y Docker Compose, o una instancia de PostgreSQL.

### 1. Backend y PostgreSQL con Docker

```bash
git clone https://github.com/LeandroMelchiori/literalura.git
cd literalura
docker compose up --build
```

La API estará disponible en:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/actuator/health`

### 2. Frontend

En otra terminal:

```bash
cd frontend
npm install
npm run dev
```

El frontend se ejecuta en `http://localhost:5173` y, en desarrollo, redirige las solicitudes `/api` al backend local.

### Backend sin Docker

```bash
export DB_URL=jdbc:postgresql://localhost:5432/literalura
export DB_USER=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=una-clave-segura-de-al-menos-32-caracteres
./mvnw spring-boot:run
```

> Los usuarios iniciales y sus contraseñas deben configurarse mediante variables de entorno. No se deben reutilizar las credenciales de desarrollo en un entorno publicado.

---

## ⚙️ Variables de entorno principales

| Variable | Descripción |
|---|---|
| `DB_URL` | URL JDBC de PostgreSQL. |
| `DB_USER` | Usuario de la base de datos. |
| `DB_PASSWORD` | Contraseña de la base de datos. |
| `JWT_SECRET` | Clave utilizada para firmar los tokens. |
| `ADMIN_USERNAME` | Username del administrador inicial. |
| `ADMIN_PASSWORD` | Contraseña del administrador inicial. |
| `DEMO_ENABLED` | Activa o desactiva el usuario bibliotecario de demostración. |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos para el frontend. |
| `VITE_API_URL` | URL de la API consumida por el frontend desplegado. |

---

## 📡 API y documentación

La API utiliza el prefijo `/api` y está organizada en los siguientes grupos:

- `/api/auth`: autenticación y usuarios internos.
- `/api/books` y `/api/authors`: catálogo e importación desde Gutendex.
- `/api/copies`: ejemplares físicos.
- `/api/members`: socios.
- `/api/loans`: préstamos y renovaciones.
- `/api/reservations`: reservas.
- `/api/fines`: multas.

La especificación completa puede consultarse desde Swagger UI cuando el backend está en ejecución.

---

## ✅ Calidad y pruebas

### Backend

```bash
./mvnw verify
```

La suite cubre servicios, controladores, reglas de negocio, seguridad, permisos y flujos de integración. Los tests utilizan H2 en memoria y no requieren PostgreSQL.

### Frontend

```bash
cd frontend
npm test
npm run build
```

El frontend incluye pruebas con Vitest y Testing Library para autenticación, componentes y flujos principales.

GitHub Actions valida el proyecto en cada push y pull request.

---

## ☁️ Despliegue

El repositorio está preparado para separar las dos capas:

- Backend y PostgreSQL en Render mediante `Dockerfile` y `render.yaml`.
- Frontend en Vercel utilizando `frontend/` como directorio raíz.
- Configuración CORS mediante `CORS_ALLOWED_ORIGINS`.
- Conexión del frontend mediante `VITE_API_URL`.

La guía paso a paso está disponible en [`docs/DEPLOY.md`](docs/DEPLOY.md).

Actualmente el repositorio no publica URLs de demostración permanentes en este README; esto evita mostrar enlaces incompletos o entornos temporales como si fueran producción.

---

## 🛣️ Próximas mejoras

- Publicar una demostración estable con cuentas separadas por rol.
- Agregar capturas o un video corto de los principales flujos.
- Incorporar tests end-to-end del frontend contra un backend de prueba.
- Añadir auditoría de operaciones sensibles.
- Mejorar observabilidad y métricas de uso de la biblioteca.

---

## Autor

Desarrollado por **Leandro Melchiori**.

- [GitHub](https://github.com/LeandroMelchiori)
- [LinkedIn](https://www.linkedin.com/in/leandromelchiori-developer/)

## Licencia

Distribuido bajo licencia MIT. Consultá [`LICENSE`](LICENSE) para más información.
