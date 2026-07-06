import { request } from './client';

export function login(username, password) {
  return request('/api/auth/login', {
    method: 'POST',
    body: { username, password },
    auth: false,
  });
}

// Gestión de usuarios del personal (solo ADMIN).
export function listUsers() {
  return request('/api/auth/users');
}

export function createUser(user) {
  return request('/api/auth/users', { method: 'POST', body: user });
}
