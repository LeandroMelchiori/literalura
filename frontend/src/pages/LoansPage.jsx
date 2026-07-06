import { useState } from 'react';
import * as library from '../api/library';
import { DataState } from '../components/DataState';
import { FormField } from '../components/FormField';
import { Pagination } from '../components/Pagination';
import { StatusBadge } from '../components/StatusBadge';
import { usePagedData } from '../hooks/usePagedData';

function LendForm({ onLent }) {
  const [copyId, setCopyId] = useState('');
  const [memberId, setMemberId] = useState('');
  const [feedback, setFeedback] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setFeedback(null);
    setSubmitting(true);
    try {
      const loan = await library.lend(Number(copyId), Number(memberId));
      setFeedback({
        ok: true,
        text: `«${loan.bookTitle}» prestado a ${loan.memberName}. Vence el ${loan.dueDate}.`,
      });
      setCopyId('');
      setMemberId('');
      onLent();
    } catch (err) {
      // Acá llegan las reglas de negocio (409): ejemplar no disponible, socio suspendido, etc.
      setFeedback({ ok: false, text: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="card form-inline" onSubmit={handleSubmit}>
      <h2>Registrar préstamo</h2>
      <div className="form-inline__row">
        <FormField
          id="loan-copy"
          label="ID del ejemplar"
          type="number"
          min="1"
          value={copyId}
          onChange={setCopyId}
        />
        <FormField
          id="loan-member"
          label="ID del socio"
          type="number"
          min="1"
          value={memberId}
          onChange={setMemberId}
        />
        <button type="submit" className="btn" disabled={submitting || !copyId || !memberId}>
          {submitting ? 'Registrando…' : 'Prestar'}
        </button>
      </div>
      {feedback && (
        <p className={feedback.ok ? 'form-ok' : 'form-error'} role="status">
          {feedback.text}
        </p>
      )}
    </form>
  );
}

export function LoansPage() {
  const [status, setStatus] = useState('');
  const { data, loading, error, page, setPage, reload } = usePagedData(
    (p) => library.listLoans(p, 10, status),
    [status],
  );
  const [actionError, setActionError] = useState(null);

  async function handleReturn(loan) {
    setActionError(null);
    try {
      await library.returnLoan(loan.id);
      reload();
    } catch (err) {
      setActionError(err);
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Préstamos</h1>
        <div className="field field--inline">
          <label htmlFor="loan-status">Estado</label>
          <select id="loan-status" value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="">Todos</option>
            <option value="ACTIVE">En curso</option>
            <option value="RETURNED">Devueltos</option>
          </select>
        </div>
      </header>

      <LendForm onLent={reload} />

      {actionError && (
        <p className="form-error" role="alert">
          {actionError.message}
        </p>
      )}

      <DataState
        loading={loading}
        error={error}
        empty={data?.content.length === 0}
        emptyMessage="No hay préstamos con ese criterio."
        onRetry={reload}
      >
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Título</th>
                <th>Inventario</th>
                <th>Socio</th>
                <th>Prestado</th>
                <th>Vence</th>
                <th>Estado</th>
                <th>
                  <span className="visually-hidden">Acciones</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {data?.content.map((loan) => (
                <tr key={loan.id}>
                  <td>{loan.id}</td>
                  <td>{loan.bookTitle}</td>
                  <td>{loan.inventoryCode}</td>
                  <td>{loan.memberName}</td>
                  <td>{loan.loanDate}</td>
                  <td>{loan.dueDate}</td>
                  <td>
                    <StatusBadge status={loan.status} overdue={loan.overdue} />
                  </td>
                  <td>
                    {loan.status === 'ACTIVE' && (
                      <button
                        type="button"
                        className="btn btn--secondary btn--small"
                        onClick={() => handleReturn(loan)}
                      >
                        Devolver
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
