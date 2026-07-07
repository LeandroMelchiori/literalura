import { useCallback, useEffect, useState } from 'react';
import * as fines from '../api/fines';
import { DataState } from '../components/DataState';

export function MyFinesPage() {
  const [items, setItems] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setItems(await fines.listMyFines());
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const totalUnpaid = items
    ?.filter((f) => !f.paid)
    .reduce((sum, f) => sum + Number(f.amount), 0);

  return (
    <section>
      <header className="page-header">
        <h1>Mis multas</h1>
        {totalUnpaid > 0 && (
          <span className="badge badge--danger">
            Impago: ${totalUnpaid.toLocaleString('es')}
          </span>
        )}
      </header>

      <DataState
        loading={loading}
        error={error}
        empty={items?.length === 0}
        emptyMessage="No tenés multas. ¡Seguí devolviendo a tiempo!"
        onRetry={load}
      >
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Título</th>
                <th>Días de atraso</th>
                <th>Monto</th>
                <th>Fecha</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {items?.map((f) => (
                <tr key={f.id}>
                  <td>{f.bookTitle}</td>
                  <td>{f.daysLate}</td>
                  <td>${Number(f.amount).toLocaleString('es')}</td>
                  <td>{f.createdAt}</td>
                  <td>
                    <span className={`badge badge--${f.paid ? 'ok' : 'danger'}`}>
                      {f.paid ? 'Pagada' : 'Impaga'}
                    </span>
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
