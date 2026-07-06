const LABELS = {
  AVAILABLE: { text: 'Disponible', tone: 'ok' },
  ON_LOAN: { text: 'Prestado', tone: 'warn' },
  RETIRED: { text: 'De baja', tone: 'muted' },
  ACTIVE: { text: 'Activo', tone: 'ok' },
  SUSPENDED: { text: 'Suspendido', tone: 'danger' },
  RETURNED: { text: 'Devuelto', tone: 'muted' },
  OVERDUE: { text: 'Vencido', tone: 'danger' },
};

export function StatusBadge({ status, overdue = false }) {
  // Un préstamo activo pero vencido se muestra como vencido.
  const key = status === 'ACTIVE' && overdue ? 'OVERDUE' : status;
  const { text, tone } = LABELS[key] ?? { text: status, tone: 'muted' };
  return <span className={`badge badge--${tone}`}>{text}</span>;
}
