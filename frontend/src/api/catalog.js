import { request } from './client';

export function listBooks(page = 0, size = 10) {
  return request(`/api/books?page=${page}&size=${size}`, { auth: false });
}

export function getStats() {
  return request('/api/books/stats', { auth: false });
}

export function searchAndCatalog(title) {
  return request(`/api/books/search?title=${encodeURIComponent(title)}`, {
    method: 'POST',
  });
}
