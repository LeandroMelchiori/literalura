import { useEffect, useState } from 'react';
import * as catalog from '../api/catalog';
import * as library from '../api/library';
import { DataState } from '../components/DataState';
import { FormField } from '../components/FormField';
import { Pagination } from '../components/Pagination';
import { StatusBadge } from '../components/StatusBadge';
import { useToast } from '../context/ToastContext';
import { usePagedData } from '../hooks/usePagedData';

function RegisterCopyForm({ onRegistered }) {
  const [books, setBooks] = useState([]);
  const [bookId, setBookId] = useState('');
  const [inventoryCode, setInventoryCode] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const toast = useToast();

  useEffect(() => {
    catalog.listBooks(0, 100).then((p) => setBooks(p.content)).catch(() => {});
  }, []);

  async function handleSubmit(e) {
    e.preventDefault();
    setSubmitting(true);
    try {
      const copy = await library.registerCopy(Number(bookId), inventoryCode.trim());
      toast(`Ejemplar ${copy.inventoryCode} registrado para «${copy.bookTitle}».`);
      setBookId('');
      setInventoryCode('');
      onRegistered();
    } catch (err) {
      toast(err.message, 'danger');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="card form-inline" onSubmit={handleSubmit}>
      <h2>Registrar ejemplar</h2>
      <div className="form-inline__row">
        <div className="field">
          <label htmlFor="copy-book">Título del catálogo</label>
          <select id="copy-book" value={bookId} onChange={(e) => setBookId(e.target.value)} required>
            <option value="">Elegir título…</option>
            {books.map((b) => (
              <option key={b.id} value={b.id}>
                {b.title} — {b.author.name}
              </option>
            ))}
          </select>
        </div>
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
