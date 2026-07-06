# Guía de trabajo — Frontend (React)

Convenciones para el SPA de React que consume la API de LiteraLura.
Complementa al `CLAUDE.md` de la raíz; aplica a todo lo que viva en `frontend/`.
Es un **proyecto de portafolio**: el código lo leen reclutadores. Priorizar
claridad y prolijidad sobre atajos o cleverness.

## Stack

- **React + Vite** (JavaScript, componentes funcionales + hooks).
- Consume la API REST (`/api/books`, `/api/authors`) vía `fetch`.
- La URL base de la API se lee de una variable de entorno (`VITE_API_URL`),
  nunca hardcodeada.

## Composición de componentes

- Componentes **pequeños y con una sola responsabilidad**. Si un componente
  hace fetch, maneja estado y pinta UI compleja, dividir.
- Separar **presentación** (recibe props, sin lógica de datos) de
  **contenedores** (orquestan datos/estado). Los de presentación deben ser
  reutilizables y fáciles de testear.
- Preferir **composición sobre props booleanas** que multiplican variantes;
  usar `children` y componentes compuestos cuando aporta claridad.
- Props explícitas y con nombres descriptivos. Evitar pasar objetos enormes
  cuando el componente solo usa dos campos.
- Nada de lógica de negocio en el JSX: extraerla a funciones o hooks.

## Hooks y estado

- Lógica de datos reutilizable en **custom hooks** (`useBooks`, `useAuthors`),
  no repetida en cada componente.
- Respetar las reglas de hooks y las dependencias de `useEffect`; no silenciar
  el linter de dependencias sin una razón comentada.
- No introducir librerías de estado global hasta que el estado local o el
  paso de props se queden cortos de verdad.
- Encapsular todas las llamadas HTTP en una capa `api/`; los componentes no
  arman URLs ni parsean respuestas a mano.

## Manejo de datos: siempre los tres estados

Toda vista que trae datos maneja **loading, error y vacío** de forma explícita
y visible. Nunca dejar la UI en blanco ni tragar un error en la consola.

## UI / UX

- **Responsivo** (mobile-first) y con layout limpio.
- **Accesible**: HTML semántico, `label` asociada a cada input, foco visible,
  navegación por teclado, contraste suficiente, `aria-*` solo cuando el HTML
  semántico no alcanza.
- **Feedback inmediato**: estados de carga (spinners/skeletons), deshabilitar
  botones mientras hay una acción en curso, mensajes de error claros y
  accionables para el usuario.
- Sin dependencias de UI pesadas si un poco de CSS resuelve; mantener el bundle
  chico.

## Comentarios en el código

- **Breves y explicativos**: explican el *porqué* (una decisión, un caso borde),
  no el *qué* que ya se lee en el código.
- Una o dos líneas. Si necesita un párrafo, probablemente el componente deba
  simplificarse.
- ❌ `// setea el estado` &nbsp;&nbsp; ✅ `// El backend pagina desde 0; la UI muestra desde 1`

## Estructura

```
frontend/src/
  api/         # capa de acceso a la API (fetch encapsulado)
  hooks/       # custom hooks (useBooks, useAuthors, ...)
  components/  # componentes de presentación reutilizables
  pages/       # vistas/contenedores que orquestan datos
```

## Commits y tests

- Mismos criterios de commits que la raíz: breves, en imperativo, en español,
  **sin mencionar IA ni herramientas de asistencia**.
- Los componentes con lógica no trivial van con su test cuando se agregue el
  runner de tests (React Testing Library).
