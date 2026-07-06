import { afterEach, describe, expect, it, vi } from 'vitest';
import { ApiError, request } from './client';

function mockFetch(status, body, contentType) {
  globalThis.fetch = vi.fn().mockResolvedValue({
    ok: status < 400,
    status,
    headers: { get: () => contentType },
    json: async () => body,
  });
}

afterEach(() => {
  vi.restoreAllMocks();
  localStorage.clear();
});

describe('request', () => {
  it('devuelve el cuerpo JSON en respuestas exitosas', async () => {
    mockFetch(200, { title: 'Emma' }, 'application/json');
    await expect(request('/api/books', { auth: false })).resolves.toEqual({ title: 'Emma' });
  });

  it('traduce un ProblemDetail de error a ApiError con su detalle', async () => {
    mockFetch(409, { status: 409, title: 'Conflicto', detail: 'El ejemplar no está disponible.' },
      'application/problem+json');

    await expect(request('/api/loans', { method: 'POST', body: {} })).rejects.toMatchObject({
      status: 409,
      message: 'El ejemplar no está disponible.',
    });
  });

  it('adjunta el token Bearer cuando hay sesión', async () => {
    localStorage.setItem('literalura.token', 'abc123');
    mockFetch(200, {}, 'application/json');

    await request('/api/loans');

    const [, options] = globalThis.fetch.mock.calls[0];
    expect(options.headers.Authorization).toBe('Bearer abc123');
  });

  it('ApiError expone el status', () => {
    const err = new ApiError(404, 'No encontrado', 'No existe');
    expect(err.status).toBe(404);
    expect(err.message).toBe('No existe');
  });
});
