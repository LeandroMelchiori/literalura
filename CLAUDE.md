# Guía de trabajo — LiteraLura

Instrucciones para cualquier sesión de desarrollo en este repositorio.
Este es un **proyecto de portafolio**: el código, los commits y la documentación
los leen reclutadores. Priorizar claridad y prolijidad sobre atajos.

## Mentalidad

- Actuar como **programador senior**: pensar el diseño antes de escribir, elegir
  la solución simple sobre la ingeniosa, y no dejar deuda técnica silenciosa.
- Ante una decisión no obvia, explicar el porqué (en el commit o en un comentario),
  no solo el qué.
- No introducir dependencias ni abstracciones que no se usen todavía.

## Buenas prácticas de código

- **Inyección por constructor** (nunca `@Autowired` en campos).
- Una responsabilidad por clase; la lógica de negocio vive en `service/`, no en
  los controladores ni en las entidades.
- DTOs para entrada/salida de la API; nunca exponer entidades JPA directamente.
- Errores mediante excepciones tipadas + `GlobalExceptionHandler`; nunca tragar
  excepciones ni devolver `null` como señal de error.
- Nombres descriptivos y consistentes con el código existente (mezcla es/en ya
  presente: mantener el estilo del archivo que se edita).
- Sin código muerto, imports sin usar, ni `System.out.println` de depuración.

## Comentarios en el código

- **Breves y explicativos**: explican el *porqué* (una decisión, un caso borde),
  no el *qué* que ya se lee en el código.
- Una o dos líneas. Si un comentario necesita un párrafo, probablemente el código
  deba simplificarse.
- Javadoc corto solo en métodos públicos con lógica no trivial.
- ❌ `// incrementa i` &nbsp;&nbsp; ✅ `// Gutendex usa snake_case; mapear download_count`

## UX / UI

- **API**: respuestas consistentes, códigos HTTP correctos (200/201/400/404/502),
  errores con `ProblemDetail` (RFC 7807) y mensajes claros para el consumidor.
  Mantener la documentación OpenAPI/Swagger al día con cada endpoint nuevo.
- **Si se agrega frontend**: diseño limpio y responsivo, accesible (contraste,
  labels, navegación por teclado), estados de carga y de error visibles, y
  feedback inmediato a las acciones del usuario.

## Commits

- **Breves y explicativos** — los ven los reclutadores.
- Título en **imperativo**, en español, ≤ 72 caracteres
  (ej: `Agregar filtro de libros por idioma`).
- El título dice *qué* cambia; el cuerpo (si hace falta) dice *por qué*.
- Un commit = un cambio lógico coherente. No mezclar refactors con features.
- No incluir identificadores de modelo ni detalles internos de la herramienta.

## Tests

- Todo cambio en la lógica de negocio va con su test (Mockito para unidad,
  MockMvc para la capa web; H2 en memoria, sin depender de PostgreSQL).
- Ejecutar `./mvnw verify` antes de commitear; no commitear con tests en rojo.

## Comandos

```bash
./mvnw verify              # build + tests
./mvnw spring-boot:run     # levantar local (requiere PostgreSQL)
docker compose up --build  # levantar app + base de datos
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Estructura

```
config/  controller/  dto/  exception/  model/  repository/  service/
```
Respetar esta separación por capas al agregar código nuevo.
