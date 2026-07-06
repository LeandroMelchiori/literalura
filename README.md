# 📚 LiteraLura API

[![CI](https://github.com/LeandroMelchiori/literalura/actions/workflows/ci.yml/badge.svg)](https://github.com/LeandroMelchiori/literalura/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-green)
![License](https://img.shields.io/badge/license-MIT-blue)

**LiteraLura** es una **API REST** de gestión de biblioteca construida con Spring Boot.
Cataloga títulos (importándolos del [Proyecto Gutenberg](https://gutenberg.org/) vía la
API [Gutendex](https://gutendex.com/)), administra los **ejemplares físicos** de cada
título, los **socios** de la biblioteca y el ciclo de **préstamos y devoluciones**, con
sus reglas de negocio. Incluye documentación interactiva con Swagger, manejo de errores
centralizado, migraciones versionadas, tests automatizados, Docker y CI.

> Originalmente un desafío de consola de Alura, reconvertido primero en una API REST
> y luego en un sistema de gestión de biblioteca con un dominio real.

---

## 🌐 Demo en vivo

<!-- Reemplazar con las URLs reales tras desplegar (ver docs/DEPLOY.md). -->

- **Aplicación:** _(pendiente — completar con la URL de Vercel)_
- **API + Swagger:** _(pendiente — completar con la URL de Render + `/swagger-ui.html`)_

**Usuario de demostración** (rol bibliotecario, para probar la operación completa):

| Usuario | Contraseña |
|---------|-----------|
| `demo`  | `demo12345` |

> El catálogo es de lectura pública; iniciar sesión con el usuario de demostración
> habilita ejemplares, socios y préstamos. El plan gratuito de Render duerme el
> servicio tras inactividad: la primera petición puede tardar unos segundos.

---

## ✨ Características

**Catálogo (Proyecto Gutenberg)**
- 🔎 **Búsqueda e importación** de títulos por nombre desde Gutendex.
- 📚 **Listado** de títulos con filtro por idioma y **estadísticas** de descargas.
- 👤 **Consulta de autores**, incluyendo autores vivos en un año dado.

**Gestión de biblioteca**
- 📗 **Ejemplares**: registro de copias físicas por título, con estado (disponible /
  prestado / dado de baja).
- 🧑‍🤝‍🧑 **Socios**: alta con validación de email/documento únicos y estado (activo /
  suspendido).
- 🔁 **Préstamos y devoluciones** con reglas de negocio: solo se presta un ejemplar
  disponible, a un socio activo y sin préstamos vencidos; la devolución libera el
  ejemplar. Listado de préstamos vencidos.

**Ingeniería**
- 🔐 **Autenticación JWT** con Spring Security y roles (`ADMIN` / `LIBRARIAN`):
  catálogo de lectura pública, operación de biblioteca autenticada, gestión de
  usuarios solo para ADMIN. Contraseñas con BCrypt.
- 📖 **Documentación OpenAPI / Swagger UI** autogenerada, con soporte de Bearer token.
- 🛡️ **Manejo de errores** consistente con `ProblemDetail` (RFC 7807); `409` para
  violaciones de reglas de negocio, `401/403` también en formato problem.
- 📄 **Paginación** en los listados (`?page=&size=&sort=`).
- 🗃️ **Migraciones de esquema versionadas** con Flyway.
- ✅ **Tests** unitarios (Mockito), de capa web (MockMvc) y de integración del flujo
  de seguridad, sobre H2.
- 🐳 **Docker + Docker Compose** y 🚀 **CI en GitHub Actions** + blueprint para Render.

---

## 🛠️ Stack

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.4 (Web, Data JPA, Validation, Actuator) |
| Seguridad | Spring Security + JWT (jjwt), BCrypt |
| Base de datos | PostgreSQL (H2 en tests) |
| Migraciones | Flyway |
| Documentación | springdoc-openapi (Swagger UI) |
| Cliente HTTP | `java.net.http.HttpClient` |
| Tests | JUnit 5, Mockito, Spring MockMvc |
| Build | Maven |
| Frontend | React 18 + Vite (SPA en `frontend/`) |
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

**Autenticación** 🔓 = público &nbsp; 🔒 = requiere JWT &nbsp; 👑 = solo ADMIN

| Método | Ruta | Descripción |
|--------|------|-------------|
| 🔓 `POST` | `/api/auth/login` | Login del personal; devuelve el JWT |
| 👑 `POST` | `/api/auth/users` | Crea un usuario del personal (`ADMIN`/`LIBRARIAN`) |

**Catálogo** (lectura pública; catalogar requiere JWT)

| Método | Ruta | Descripción |
|--------|------|-------------|
| 🔒 `POST` | `/api/books/search?title={título}` | Busca en Gutendex y cataloga el título |
| `GET`  | `/api/books` | Lista paginada de títulos |
| `GET`  | `/api/books/language/{idioma}` | Títulos por idioma (`en`, `es`, ...), paginado |
| `GET`  | `/api/books/stats` | Estadísticas agregadas de descargas |
| `GET`  | `/api/authors` | Lista paginada de autores |
| `GET`  | `/api/authors/alive?year={año}` | Autores vivos hasta ese año |

**Biblioteca** (todo requiere JWT)

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/copies` | Registra un ejemplar físico de un título |
| `GET`  | `/api/copies?status={estado}` | Lista ejemplares, filtrable por estado |
| `POST` | `/api/members` | Da de alta un socio |
| `GET`  | `/api/members` | Lista paginada de socios |
| `PATCH`| `/api/members/{id}/status?status={estado}` | Activa o suspende un socio |
| `POST` | `/api/loans` | Registra un préstamo (`{copyId, memberId}`) |
| `POST` | `/api/loans/{id}/return` | Registra la devolución |
| `GET`  | `/api/loans?status=&memberId=` | Lista préstamos (por estado o socio) |
| `GET`  | `/api/loans/overdue` | Lista préstamos vencidos |

Health check público: `GET /actuator/health`.

### Documentación interactiva

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

### Ejemplo: flujo completo

```bash
# 1. Login (al primer arranque se crea el ADMIN inicial: admin / admin12345)
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin12345"}' | jq -r .token)

# 2. Catalogar un título desde Gutendex
curl -X POST "http://localhost:8080/api/books/search?title=pride%20and%20prejudice" \
  -H "Authorization: Bearer $TOKEN"

# 3. Registrar un ejemplar físico, un socio y un préstamo
curl -X POST http://localhost:8080/api/copies -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" -d '{"bookId":1,"inventoryCode":"A-001"}'
curl -X POST http://localhost:8080/api/members -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana Díaz","email":"ana@mail.com","documentId":"12345678"}'
curl -X POST http://localhost:8080/api/loans -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" -d '{"copyId":1,"memberId":1}'
```

> ⚠️ En producción definir `JWT_SECRET`, `ADMIN_USERNAME` y `ADMIN_PASSWORD`
> mediante variables de entorno; los valores por defecto son solo para desarrollo.

---

## 💻 Frontend (React)

SPA en [`frontend/`](frontend/) que consume esta API: catálogo público, login del
personal y operación de la biblioteca (ejemplares, socios, préstamos) con manejo
visible de estados de carga, error y reglas de negocio.

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173 (proxya /api al backend local)
```

En producción el frontend se despliega aparte (p. ej. Vercel) definiendo
`VITE_API_URL` con el dominio de la API, cuyo CORS se configura con
`CORS_ALLOWED_ORIGINS`.

La guía completa de despliegue (Render + Vercel) está en
[`docs/DEPLOY.md`](docs/DEPLOY.md).

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
