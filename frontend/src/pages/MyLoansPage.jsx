import * as library from '../api/library';
import { DataState } from '../components/DataState';
import { Pagination } from '../components/Pagination';
import { StatusBadge } from '../components/StatusBadge';
import { useToast } from '../context/ToastContext';
import { usePagedData } from '../hooks/usePagedData';
import { dueLabel, formatDate } from '../utils/dates';

const MAX_RENEWALS = 2;

export function MyLoansPage() {
  const { data, loading, error, page, setPage, reload } = usePagedData(
    (p) => library.listMyLoans(p),
  );
  const toast = useToast();

  async function renew(loan) {
    try {
      const updated = await library.renewLoan(loan.id);
      toast(`Renovaste «${loan.bookTitle}»; ahora vence el ${formatDate(updated.dueDate)}.`);
      reload();
    } catch (err) {
      // Reglas: vencido, con reserva pendiente, o máximo de renovaciones.
      toast(err.message, 'danger');
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Mis préstamos</h1>
      </header>

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
                  <td>{formatDate(loan.loanDate)}</td>
                  <td>
                    {formatDate(loan.dueDate)}
                    {loan.status === 'ACTIVE' && (
                      <div className={`muted due-hint ${loan.overdue ? 'due-hint--danger' : ''}`}>
                        {dueLabel(loan.dueDate)}
                      </div>
                    )}
                  </td>
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
