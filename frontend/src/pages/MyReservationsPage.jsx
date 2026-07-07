import { useCallback, useEffect, useState } from 'react';
import * as reservations from '../api/reservations';
import { DataState } from '../components/DataState';

export function MyReservationsPage() {
  const [items, setItems] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionError, setActionError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setItems(await reservations.listMyReservations());
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  async function cancel(id) {
    setActionError(null);
    try {
      await reservations.cancelReservation(id);
      load();
    } catch (e) {
      setActionError(e);
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Mis reservas</h1>
      </header>

      {actionError && (
        <p className="form-error" role="alert">
          {actionError.message}
        </p>
      )}

      <DataState
        loading={loading}
        error={error}
        empty={items?.length === 0}
        emptyMessage="No tenés reservas. Reservá un título desde el catálogo."
        onRetry={load}
      >
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Título</th>
                <th>Fecha</th>
                <th>Estado</th>
                <th>
                  <span className="visually-hidden">Acciones</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {items?.map((r) => (
                <tr key={r.id}>
                  <td>{r.bookTitle}</td>
                  <td>{r.reservationDate}</td>
                  <td>
                    <ReservationBadge status={r.status} />
                  </td>
                  <td>
                    {r.status === 'PENDING' && (
                      <button
                        type="button"
                        className="btn btn--secondary btn--small"
                        onClick={() => cancel(r.id)}
                      >
                        Cancelar
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </DataState>
    </section>
  );
}

function ReservationBadge({ status }) {
  const map = {
    PENDING: { text: 'Pendiente', tone: 'warn' },
    FULFILLED: { text: 'Cumplida', tone: 'ok' },
    CANCELLED: { text: 'Cancelada', tone: 'muted' },
  };
  const { text, tone } = map[status] ?? { text: status, tone: 'muted' };
  return <span className={`badge badge--${tone}`}>{text}</span>;
}
