import * as library from '../api/library';
import { DataState } from '../components/DataState';
import { Pagination } from '../components/Pagination';
import { StatusBadge } from '../components/StatusBadge';
import { usePagedData } from '../hooks/usePagedData';

export function MyLoansPage() {
  const { data, loading, error, page, setPage, reload } = usePagedData(
    (p) => library.listMyLoans(p),
  );

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
                <th>Devuelto</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {data?.content.map((loan) => (
                <tr key={loan.id}>
                  <td>{loan.bookTitle}</td>
                  <td>{loan.loanDate}</td>
                  <td>{loan.dueDate}</td>
                  <td>{loan.returnDate ?? '—'}</td>
                  <td>
                    <StatusBadge status={loan.status} overdue={loan.overdue} />
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
