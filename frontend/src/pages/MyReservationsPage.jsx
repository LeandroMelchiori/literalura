import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import * as reservations from '../api/reservations';
import { ConfirmDialog } from '../components/ConfirmDialog';
import { DataState } from '../components/DataState';
import { useToast } from '../context/ToastContext';
import { formatDate } from '../utils/dates';

export function MyReservationsPage() {
  const [items, setItems] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  // Reserva pendiente de confirmación de cancelación (null = diálogo cerrado).
  const [toCancel, setToCancel] = useState(null);
  const toast = useToast();

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

  async function cancel(reservation) {
    try {
      await reservations.cancelReservation(reservation.id);
      toast(`Reserva de «${reservation.bookTitle}» cancelada.`);
      load();
    } catch (e) {
      toast(e.message, 'danger');
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Mis reservas</h1>
      </header>

      <DataState
        loading={loading}
        error={error}
        empty={items?.length === 0}
        emptyMessage="No tenés reservas."
        emptyAction={
          <Link to="/" className="btn">
            Reservar desde el catálogo
          </Link>
        }
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
                  <td>{formatDate(r.reservationDate)}</td>
                  <td>
                    <ReservationBadge status={r.status} />
                  </td>
                  <td>
                    {r.status === 'PENDING' && (
                      <button
                        type="button"
                        className="btn btn--secondary btn--small"
                        onClick={() => setToCancel(r)}
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

      <ConfirmDialog
        open={toCancel !== null}
        title="Cancelar reserva"
        message={`¿Cancelar la reserva de «${toCancel?.bookTitle}»? Si te arrepentís, vas a tener que reservarlo de nuevo.`}
        confirmLabel="Cancelar reserva"
        onConfirm={() => {
          cancel(toCancel);
          setToCancel(null);
        }}
        onCancel={() => setToCancel(null)}
      />
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
