import { request } from './client';

// Ejemplares
export function listCopies(page = 0, size = 10, status = '', bookId = '') {
  const filters = `${status ? `&status=${status}` : ''}${bookId ? `&bookId=${bookId}` : ''}`;
  return request(`/api/copies?page=${page}&size=${size}${filters}`);
}

export function registerCopy(bookId, inventoryCode) {
  return request('/api/copies', { method: 'POST', body: { bookId, inventoryCode } });
}

// Socios
export function listMembers(page = 0, size = 10, search = '') {
  const filter = search ? `&search=${encodeURIComponent(search)}` : '';
  return request(`/api/members?page=${page}&size=${size}${filter}`);
}

export function registerMember(member) {
  return request('/api/members', { method: 'POST', body: member });
}

export function changeMemberStatus(id, status) {
  return request(`/api/members/${id}/status?status=${status}`, { method: 'PATCH' });
}

// Préstamos
export function listLoans(page = 0, size = 10, status = '') {
  const filter = status ? `&status=${status}` : '';
  return request(`/api/loans?page=${page}&size=${size}${filter}`);
}

// Préstamos del cliente autenticado.
export function listMyLoans(page = 0, size = 10) {
  return request(`/api/loans/mine?page=${page}&size=${size}`);
}

export function renewLoan(loanId) {
  return request(`/api/loans/${loanId}/renew`, { method: 'POST' });
}

export function listOverdueLoans() {
  return request('/api/loans/overdue');
}

export function lend(copyId, memberId) {
  return request('/api/loans', { method: 'POST', body: { copyId, memberId } });
}

export function returnLoan(loanId) {
  return request(`/api/loans/${loanId}/return`, { method: 'POST' });
}
