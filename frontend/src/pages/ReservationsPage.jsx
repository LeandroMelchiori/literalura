import { useCallback, useEffect, useState } from 'react';
import * as library from '../api/library';
import * as reservations from '../api/reservations';
import { DataState } from '../components/DataState';
import { useToast } from '../context/ToastContext';
import { formatDate } from '../utils/dates';

export function ReservationsPage() {
  const [items, setItems] = useState(null);
  const [availableCopies, setAvailableCopies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  // Ejemplar elegido por reserva (id de reserva -> id de ejemplar).
  const [selection, setSelection] = useState({});
  const toast = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [pending, copies] = await Promise.all([
        reservations.listPendingReservations(),
        library.listCopies(0, 200, 'AVAILABLE'),
      ]);
      setItems(pending);
      setAvailableCopies(copies.content);
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
    try {
      await reservations.fulfillReservation(reservation.id, Number(selection[reservation.id]));
      toast(`Reserva de «${reservation.bookTitle}» cumplida: prestado a ${reservation.memberName}.`);
      load();
    } catch (e) {
      toast(e.message, 'danger');
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Reservas pendientes</h1>
      </header>

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
                <th>Ejemplar a prestar</th>
                <th>
                  <span className="visually-hidden">Acciones</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {items?.map((r) => {
                // Solo los ejemplares disponibles del título reservado.
                const options = availableCopies.filter((c) => c.bookId === r.bookId);
                return (
                  <tr key={r.id}>
                    <td>{r.bookTitle}</td>
                    <td>{r.memberName}</td>
                    <td>{formatDate(r.reservationDate)}</td>
                    <td>
                      {options.length === 0 ? (
                        <span className="muted">Sin ejemplares disponibles</span>
                      ) : (
                        <select
                          aria-label={`Ejemplar para la reserva de ${r.bookTitle}`}
                          value={selection[r.id] ?? ''}
                          onChange={(e) =>
                            setSelection((prev) => ({ ...prev, [r.id]: e.target.value }))
                          }
                        >
                          <option value="">Elegir…</option>
                          {options.map((c) => (
                            <option key={c.id} value={c.id}>
                              {c.inventoryCode}
                            </option>
                          ))}
                        </select>
                      )}
                    </td>
                    <td>
                      <button
                        type="button"
                        className="btn btn--small"
                        disabled={!selection[r.id]}
                        onClick={() => fulfill(r)}
                      >
                        Prestar
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </DataState>
    </section>
  );
}
