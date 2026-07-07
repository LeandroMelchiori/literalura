import { useState } from 'react';
import * as library from '../api/library';
import { DataState } from '../components/DataState';
import { Pagination } from '../components/Pagination';
import { StatusBadge } from '../components/StatusBadge';
import { usePagedData } from '../hooks/usePagedData';

const MAX_RENEWALS = 2;

export function MyLoansPage() {
  const { data, loading, error, page, setPage, reload } = usePagedData(
    (p) => library.listMyLoans(p),
  );
  const [feedback, setFeedback] = useState(null);

  async function renew(loan) {
    setFeedback(null);
    try {
      await library.renewLoan(loan.id);
      setFeedback({ ok: true, text: `Renovaste «${loan.bookTitle}».` });
      reload();
    } catch (err) {
      // Reglas: vencido, con reserva pendiente, o máximo de renovaciones.
      setFeedback({ ok: false, text: err.message });
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Mis préstamos</h1>
      </header>

      {feedback && (
        <p className={feedback.ok ? 'form-ok' : 'form-error'} role="status">
          {feedback.text}
        </p>
      )}

      <DataState
        loading={loading}
        error={error}
        empty={data?.content.length === 0}
        emptyMessage="Todavía no tenés préstamos."
        onRetry={reload}
      >
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Título</th>
                <th>Prestado</th>
                <th>Vence</th>
                <th>Renovaciones</th>
                <th>Estado</th>
                <th>
                  <span className="visually-hidden">Acciones</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {data?.content.map((loan) => (
                <tr key={loan.id}>
                  <td>{loan.bookTitle}</td>
                  <td>{loan.loanDate}</td>
                  <td>{loan.dueDate}</td>
                  <td>{loan.renewals} / {MAX_RENEWALS}</td>
                  <td>
                    <StatusBadge status={loan.status} overdue={loan.overdue} />
                  </td>
                  <td>
                    {loan.status === 'ACTIVE' && !loan.overdue && loan.renewals < MAX_RENEWALS && (
                      <button
                        type="button"
                        className="btn btn--secondary btn--small"
                        onClick={() => renew(loan)}
                      >
                        Renovar
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <Pagination page={page} totalPages={data?.totalPages ?? 0} onChange={setPage} />
      </DataState>
    </section>
  );
}
