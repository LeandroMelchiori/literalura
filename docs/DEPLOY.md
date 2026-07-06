# Guía de despliegue

La aplicación se despliega en dos servicios:

- **Backend (API + PostgreSQL)** → [Render](https://render.com) vía el `Dockerfile` y `render.yaml`.
- **Frontend (SPA React)** → [Vercel](https://vercel.com), que compila `frontend/`.

El orden importa: primero el backend (para tener su URL), después el frontend,
y al final se conecta el CORS.

---

## 1. Backend en Render

1. Crear una cuenta en Render y conectar la cuenta de GitHub.
2. **New → Blueprint** y elegir el repositorio `literalura`. Render detecta
   `render.yaml` y propone crear el servicio web + la base de datos PostgreSQL.
3. Antes de aplicar, completar las variables marcadas como *sync: false*:
   - `ADMIN_PASSWORD`: la contraseña del usuario `admin` inicial (elegir una propia).
   - `CORS_ALLOWED_ORIGINS`: dejar en blanco por ahora; se completa en el paso 3.
   - (`JWT_SECRET` lo genera Render automáticamente.)
4. **Apply**. Render construye la imagen, aplica las migraciones Flyway y levanta la API.
   La URL queda como `https://literalura-api.onrender.com` (o similar).
5. Verificar: `https://<tu-api>.onrender.com/swagger-ui.html` debe abrir la documentación.

> El plan gratuito de Render duerme el servicio tras inactividad; la primera
> petición luego de un rato puede tardar unos segundos en responder.

---

## 2. Frontend en Vercel

1. Crear una cuenta en Vercel y conectar GitHub.
2. **Add New → Project** y elegir el repositorio `literalura`.
3. Configurar:
   - **Root Directory**: `frontend`
   - Framework preset: **Vite** (se detecta solo).
4. En **Environment Variables** agregar:
   - `VITE_API_URL` = la URL del backend de Render (paso 1), sin barra final.
5. **Deploy**. Vercel compila el SPA y publica en `https://literalura.vercel.app` (o similar).

---

## 3. Conectar el CORS

1. Volver al servicio de Render → **Environment**.
2. Editar `CORS_ALLOWED_ORIGINS` con la URL de Vercel (paso 2), sin barra final.
   Para varios orígenes, separarlos con coma.
3. Guardar; Render reinicia el servicio con el nuevo valor.

Listo: el frontend en Vercel consume la API en Render con autenticación JWT.
Iniciar sesión con `admin` y la `ADMIN_PASSWORD` definida en el paso 1.

---

## Despliegue continuo

Ambos servicios quedan enlazados al repositorio: cada push a `main` redepliega
automáticamente el backend en Render y el frontend en Vercel.

## Variables de entorno de referencia

| Variable | Servicio | Descripción |
|----------|----------|-------------|
| `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USER` / `DB_PASSWORD` | Render | Conexión a PostgreSQL (las provee Render) |
| `JWT_SECRET` | Render | Clave para firmar los JWT (≥ 32 caracteres) |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` | Render | Credenciales del ADMIN inicial |
| `CORS_ALLOWED_ORIGINS` | Render | Orígenes del frontend permitidos |
| `VITE_API_URL` | Vercel | URL base de la API que consume el SPA |
