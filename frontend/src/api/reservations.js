import { request } from './client';

// Cliente
export function reserve(bookId) {
  return request('/api/reservations', { method: 'POST', body: { bookId } });
}

export function listMyReservations() {
  return request('/api/reservations/mine');
}

export function cancelReservation(id) {
  return request(`/api/reservations/${id}/cancel`, { method: 'POST' });
}

// Personal
export function listPendingReservations() {
  return request('/api/reservations');
}

export function fulfillReservation(id, copyId) {
  return request(`/api/reservations/${id}/fulfill?copyId=${copyId}`, { method: 'POST' });
}
