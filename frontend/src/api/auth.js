import { request } from './client';

export function login(username, password) {
  return request('/api/auth/login', {
    method: 'POST',
    body: { username, password },
    auth: false,
  });
}
