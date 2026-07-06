// Capa única de acceso HTTP: los componentes nunca llaman a fetch directamente.
const BASE_URL = import.meta.env.VITE_API_URL ?? '';

const TOKEN_KEY = 'literalura.token';

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}

/** Error con el detalle del ProblemDetail (RFC 7807) que devuelve la API. */
export class ApiError extends Error {
  constructor(status, title, detail) {
    super(detail || title || `Error HTTP ${status}`);
    this.status = status;
    this.title = title;
  }
}

export async function request(path, { method = 'GET', body, auth = true } = {}) {
  const headers = {};
  if (body !== undefined) headers['Content-Type'] = 'application/json';

  const token = getToken();
  if (auth && token) headers['Authorization'] = `Bearer ${token}`;

  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (response.status === 204) return null;

  const isProblem = response.headers.get('Content-Type')?.includes('problem+json');
  const data = await response.json().catch(() => null);

  if (!response.ok) {
    if (isProblem && data) throw new ApiError(data.status, data.title, data.detail);
    throw new ApiError(response.status, 'Error', 'Ocurrió un error inesperado.');
  }
  return data;
}
