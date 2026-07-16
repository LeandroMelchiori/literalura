import { request } from './client';

export function listBooks(page = 0, size = 10, search = '') {
  const filter = search ? `&search=${encodeURIComponent(search)}` : '';
  return request(`/api/books?page=${page}&size=${size}${filter}`, { auth: false });
}

export function getStats() {
  return request('/api/books/stats', { auth: false });
}

export function searchAndCatalog(title) {
  return request(`/api/books/search?title=${encodeURIComponent(title)}`, {
    method: 'POST',
  });
}
