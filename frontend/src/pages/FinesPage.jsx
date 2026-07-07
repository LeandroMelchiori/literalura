import { useCallback, useEffect, useState } from 'react';
import * as fines from '../api/fines';
import { DataState } from '../components/DataState';

export function FinesPage() {
  const [items, setItems] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionError, setActionError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setItems(await fines.listUnpaidFines());
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  async function pay(fine) {
    setActionError(null);
    try {
      await fines.payFine(fine.id);
      load();
    } catch (e) {
      setActionError(e);
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Multas impagas</h1>
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
        emptyMessage="No hay multas impagas."
        onRetry={load}
      >
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Socio</th>
                <th>Título</th>
                <th>Días de atraso</th>
                <th>Monto</th>
                <th>Fecha</th>
                <th>
                  <span className="visually-hidden">Acciones</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {items?.map((f) => (
                <tr key={f.id}>
                  <td>{f.memberName}</td>
                  <td>{f.bookTitle}</td>
                  <td>{f.daysLate}</td>
                  <td>${Number(f.amount).toLocaleString('es')}</td>
                  <td>{f.createdAt}</td>
                  <td>
                    <button type="button" className="btn btn--small" onClick={() => pay(f)}>
                      Registrar pago
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
