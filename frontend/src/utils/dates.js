// Fechas ISO (YYYY-MM-DD) → texto humano. Se parsea manualmente para evitar
// el corrimiento de día que produce new Date('YYYY-MM-DD') (lo toma como UTC).
function parseISO(iso) {
  const [y, m, d] = iso.split('-').map(Number);
  return new Date(y, m - 1, d);
}

export function formatDate(iso) {
  if (!iso) return '—';
  return parseISO(iso).toLocaleDateString('es', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  });
}

/** Días entre hoy y la fecha (negativo si ya pasó). */
export function daysUntil(iso) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return Math.round((parseISO(iso) - today) / 86_400_000);
}

/** Texto relativo para vencimientos: "vence hoy", "vence en 3 días", "venció hace 2 días". */
export function dueLabel(iso) {
  const days = daysUntil(iso);
  if (days === 0) return 'vence hoy';
  if (days === 1) return 'vence mañana';
  if (days > 1) return `vence en ${days} días`;
  if (days === -1) return 'venció ayer';
  return `venció hace ${-days} días`;
}
