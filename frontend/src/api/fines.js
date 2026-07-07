import { request } from './client';

// Cliente
export function listMyFines() {
  return request('/api/fines/mine');
}

// Personal
export function listUnpaidFines() {
  return request('/api/fines');
}

export function payFine(id) {
  return request(`/api/fines/${id}/pay`, { method: 'POST' });
}
