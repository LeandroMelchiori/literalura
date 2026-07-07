import { useEffect, useState } from 'react';
import * as catalog from '../api/catalog';
import * as reservationsApi from '../api/reservations';
import { DataState } from '../components/DataState';
import { FormField } from '../components/FormField';
import { Pagination } from '../components/Pagination';
import { useAuth } from '../context/AuthContext';
import { usePagedData } from '../hooks/usePagedData';

function StatsBar() {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    catalog.getStats().then(setStats).catch(() => setStats(null));
  }, []);

  if (!stats) return null;
  return (
    <div className="stats">
      <div className="stat">
        <strong>{stats.totalBooks}</strong>
        <span>Títulos</span>
      </div>
      <div className="stat">
        <strong>{stats.totalAuthors}</strong>
        <span>Autores</span>
      </div>
      <div className="stat">
        <strong>{Math.round(stats.averageDownloads).toLocaleString('es')}</strong>
        <span>Descargas promedio</span>
      </div>
    </div>
  );
}

function CatalogForm({ onCataloged }) {
  const [title, setTitle] = useState('');
  const [feedback, setFeedback] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setFeedback(null);
    setSubmitting(true);
    try {
      const book = await catalog.searchAndCatalog(title.trim());
      setFeedback({ ok: true, text: `«${book.title}» catalogado.` });
      setTitle('');
      onCataloged();
    } catch (err) {
      setFeedback({ ok: false, text: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="card form-inline" onSubmit={handleSubmit}>
      <h2>Catalogar desde Gutendex</h2>
      <div className="form-inline__row">
        <FormField
          id="search-title"
          label="Título a buscar"
          value={title}
          onChange={setTitle}
          placeholder="Ej: Pride and Prejudice"
        />
        <button type="submit" className="btn" disabled={submitting || !title.trim()}>
          {submitting ? 'Buscando…' : 'Buscar y catalogar'}
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

export function CatalogPage() {
  const { isStaff, isClient } = useAuth();
  const { data, loading, error, page, setPage, reload } = usePagedData(
    (p) => catalog.listBooks(p),
  );
  const [reserveFeedback, setReserveFeedback] = useState(null);

  async function reservar(book) {
    setReserveFeedback(null);
    try {
      await reservationsApi.reserve(book.id);
      setReserveFeedback({ ok: true, text: `Reservaste «${book.title}».` });
    } catch (err) {
      setReserveFeedback({ ok: false, text: err.message });
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Catálogo</h1>
        <StatsBar />
      </header>

      {isStaff && <CatalogForm onCataloged={reload} />}

      {reserveFeedback && (
        <p className={reserveFeedback.ok ? 'form-ok' : 'form-error'} role="status">
          {reserveFeedback.text}
        </p>
      )}

      <DataState
        loading={loading}
        error={error}
        empty={data?.content.length === 0}
        emptyMessage="El catálogo está vacío. Catalogá el primer título desde Gutendex."
        onRetry={reload}
      >
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Título</th>
                <th>Autor</th>
                <th>Idioma</th>
                <th>Descargas</th>
                {isClient && (
                  <th>
                    <span className="visually-hidden">Acciones</span>
                  </th>
                )}
              </tr>
            </thead>
            <tbody>
              {data?.content.map((book) => (
                <tr key={book.id}>
                  <td>{book.id}</td>
                  <td>{book.title}</td>
                  <td>{book.author.name}</td>
                  <td>{book.languages.join(', ')}</td>
                  <td>{book.downloadCount.toLocaleString('es')}</td>
                  {isClient && (
                    <td>
                      <button
                        type="button"
                        className="btn btn--secondary btn--small"
                        onClick={() => reservar(book)}
                      >
                        Reservar
                      </button>
                    </td>
                  )}
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
