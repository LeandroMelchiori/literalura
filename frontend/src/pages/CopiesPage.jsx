import { useState } from 'react';
import * as library from '../api/library';
import { DataState } from '../components/DataState';
import { FormField } from '../components/FormField';
import { Pagination } from '../components/Pagination';
import { StatusBadge } from '../components/StatusBadge';
import { usePagedData } from '../hooks/usePagedData';

function RegisterCopyForm({ onRegistered }) {
  const [bookId, setBookId] = useState('');
  const [inventoryCode, setInventoryCode] = useState('');
  const [feedback, setFeedback] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setFeedback(null);
    setSubmitting(true);
    try {
      const copy = await library.registerCopy(Number(bookId), inventoryCode.trim());
      setFeedback({ ok: true, text: `Ejemplar ${copy.inventoryCode} registrado para «${copy.bookTitle}».` });
      setBookId('');
      setInventoryCode('');
      onRegistered();
    } catch (err) {
      setFeedback({ ok: false, text: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="card form-inline" onSubmit={handleSubmit}>
      <h2>Registrar ejemplar</h2>
      <div className="form-inline__row">
        <FormField
          id="copy-book-id"
          label="ID del título (ver Catálogo)"
          type="number"
          min="1"
          value={bookId}
          onChange={setBookId}
        />
        <FormField
          id="copy-code"
          label="Código de inventario"
          value={inventoryCode}
          onChange={setInventoryCode}
          placeholder="Ej: A-001"
        />
        <button type="submit" className="btn" disabled={submitting || !bookId || !inventoryCode.trim()}>
          {submitting ? 'Registrando…' : 'Registrar'}
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

export function CopiesPage() {
  const [status, setStatus] = useState('');
  const { data, loading, error, page, setPage, reload } = usePagedData(
    (p) => library.listCopies(p, 10, status),
    [status],
  );

  return (
    <section>
      <header className="page-header">
        <h1>Ejemplares</h1>
        <div className="field field--inline">
          <label htmlFor="copy-status">Estado</label>
          <select id="copy-status" value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="">Todos</option>
            <option value="AVAILABLE">Disponibles</option>
            <option value="ON_LOAN">Prestados</option>
            <option value="RETIRED">De baja</option>
          </select>
        </div>
      </header>

      <RegisterCopyForm onRegistered={reload} />

      <DataState
        loading={loading}
        error={error}
        empty={data?.content.length === 0}
        emptyMessage="No hay ejemplares con ese criterio."
        onRetry={reload}
      >
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Inventario</th>
                <th>Título</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {data?.content.map((copy) => (
                <tr key={copy.id}>
                  <td>{copy.id}</td>
                  <td>{copy.inventoryCode}</td>
                  <td>{copy.bookTitle}</td>
                  <td>
                    <StatusBadge status={copy.status} />
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
