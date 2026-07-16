import { useCallback, useEffect, useState } from 'react';
import * as library from '../api/library';
import { DataState } from '../components/DataState';
import { Pagination } from '../components/Pagination';
import { StatusBadge } from '../components/StatusBadge';
import { useToast } from '../context/ToastContext';
import { usePagedData } from '../hooks/usePagedData';
import { formatDate } from '../utils/dates';

/**
 * Formulario de préstamo con selectores por nombre: nadie recuerda IDs.
 * Select nativo: accesible por defecto y suficiente para el volumen de una
 * demo; con catálogos grandes el paso siguiente sería un typeahead.
 */
function LendForm({ onLent }) {
  const [members, setMembers] = useState([]);
  const [copies, setCopies] = useState([]);
  const [memberId, setMemberId] = useState('');
  const [copyId, setCopyId] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const toast = useToast();

  const loadOptions = useCallback(async () => {
    try {
      const [m, c] = await Promise.all([
        library.listMembers(0, 100),
        library.listCopies(0, 100, 'AVAILABLE'),
      ]);
      setMembers(m.content);
      setCopies(c.content);
    } catch {
      // Si falla la carga de opciones, el submit igual mostrará el error.
    }
  }, []);

  useEffect(() => {
    loadOptions();
  }, [loadOptions]);

  async function handleSubmit(e) {
    e.preventDefault();
    setSubmitting(true);
    try {
      const loan = await library.lend(Number(copyId), Number(memberId));
      toast(`«${loan.bookTitle}» prestado a ${loan.memberName}. Vence el ${formatDate(loan.dueDate)}.`);
      setCopyId('');
      setMemberId('');
      loadOptions();
      onLent();
    } catch (err) {
      toast(err.message, 'danger');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="card form-inline" onSubmit={handleSubmit}>
      <h2>Registrar préstamo</h2>
      <div className="form-inline__row">
        <div className="field">
          <label htmlFor="loan-copy">Ejemplar disponible</label>
          <select id="loan-copy" value={copyId} onChange={(e) => setCopyId(e.target.value)} required>
            <option value="">Elegir ejemplar…</option>
            {copies.map((c) => (
              <option key={c.id} value={c.id}>
                {c.inventoryCode} — {c.bookTitle}
              </option>
            ))}
          </select>
        </div>
        <div className="field">
          <label htmlFor="loan-member">Socio</label>
          <select id="loan-member" value={memberId} onChange={(e) => setMemberId(e.target.value)} required>
            <option value="">Elegir socio…</option>
            {members.map((m) => (
              <option key={m.id} value={m.id}>
                {m.name} — {m.documentId}
              </option>
            ))}
          </select>
        </div>
        <button type="submit" className="btn" disabled={submitting || !copyId || !memberId}>
          {submitting ? 'Registrando…' : 'Prestar'}
        </button>
      </div>
    </form>
  );
}

export function LoansPage() {
  const [status, setStatus] = useState('');
  const { data, loading, error, page, setPage, reload } = usePagedData(
    (p) => library.listLoans(p, 10, status),
    [status],
  );
  const toast = useToast();

  async function handleReturn(loan) {
    try {
      await library.returnLoan(loan.id);
      toast(`«${loan.bookTitle}» devuelto.`);
      reload();
    } catch (err) {
      toast(err.message, 'danger');
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
                  <td>{formatDate(loan.loanDate)}</td>
                  <td>{formatDate(loan.dueDate)}</td>
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
