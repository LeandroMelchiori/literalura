import { useCallback, useEffect, useState } from 'react';
import * as reservations from '../api/reservations';
import { DataState } from '../components/DataState';

export function ReservationsPage() {
  const [items, setItems] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  // Copia elegida por cada reserva para cumplirla (id de reserva -> código de ejemplar).
  const [copyIds, setCopyIds] = useState({});
  const [feedback, setFeedback] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setItems(await reservations.listPendingReservations());
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  async function fulfill(reservation) {
    setFeedback(null);
    const copyId = copyIds[reservation.id];
    if (!copyId) {
      setFeedback({ ok: false, text: 'Indicá el ID del ejemplar a prestar.' });
      return;
    }
    try {
      await reservations.fulfillReservation(reservation.id, Number(copyId));
      setFeedback({ ok: true, text: `Reserva de «${reservation.bookTitle}» cumplida.` });
      load();
    } catch (e) {
      setFeedback({ ok: false, text: e.message });
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Reservas pendientes</h1>
      </header>

      {feedback && (
        <p className={feedback.ok ? 'form-ok' : 'form-error'} role="status">
          {feedback.text}
        </p>
      )}

      <DataState
        loading={loading}
        error={error}
        empty={items?.length === 0}
        emptyMessage="No hay reservas pendientes."
        onRetry={load}
      >
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Título</th>
                <th>Socio</th>
                <th>Fecha</th>
                <th>ID ejemplar</th>
                <th>
                  <span className="visually-hidden">Acciones</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {items?.map((r) => (
                <tr key={r.id}>
                  <td>{r.bookTitle}</td>
                  <td>{r.memberName}</td>
                  <td>{r.reservationDate}</td>
                  <td>
                    <input
                      type="number"
                      min="1"
                      aria-label={`ID del ejemplar para la reserva de ${r.bookTitle}`}
                      value={copyIds[r.id] ?? ''}
                      onChange={(e) => setCopyIds((prev) => ({ ...prev, [r.id]: e.target.value }))}
                      style={{ width: '6rem' }}
                    />
                  </td>
                  <td>
                    <button
                      type="button"
                      className="btn btn--small"
                      onClick={() => fulfill(r)}
                    >
                      Prestar
                    </button>
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
