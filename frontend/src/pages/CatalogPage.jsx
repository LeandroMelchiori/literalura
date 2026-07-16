import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import * as catalog from '../api/catalog';
import * as fines from '../api/fines';
import * as library from '../api/library';
import * as reservationsApi from '../api/reservations';
import { DataState } from '../components/DataState';
import { FormField } from '../components/FormField';
import { Pagination } from '../components/Pagination';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { daysUntil } from '../utils/dates';
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

/** Resumen del socio al entrar: sus préstamos, vencimientos próximos y multas. */
function ClientSummary() {
  const [summary, setSummary] = useState(null);

  useEffect(() => {
    Promise.all([library.listMyLoans(0, 50), fines.listMyFines()])
      .then(([loans, myFines]) => {
        const active = loans.content.filter((l) => l.status === 'ACTIVE');
        const dueSoon = active.filter((l) => !l.overdue && daysUntil(l.dueDate) <= 3);
        const overdue = active.filter((l) => l.overdue);
        const unpaid = myFines
          .filter((f) => !f.paid)
          .reduce((sum, f) => sum + Number(f.amount), 0);
        setSummary({ active: active.length, dueSoon: dueSoon.length, overdue: overdue.length, unpaid });
      })
      .catch(() => setSummary(null));
  }, []);

  if (!summary) return null;
  return (
    <div className="card summary">
      <Link to="/my-loans" className="summary__item">
        📖 {summary.active} préstamo{summary.active === 1 ? '' : 's'} activo{summary.active === 1 ? '' : 's'}
      </Link>
      {summary.dueSoon > 0 && (
        <Link to="/my-loans" className="summary__item summary__item--warn">
          ⏰ {summary.dueSoon} vence{summary.dueSoon === 1 ? '' : 'n'} pronto
        </Link>
      )}
      {summary.overdue > 0 && (
        <Link to="/my-loans" className="summary__item summary__item--danger">
          ⚠️ {summary.overdue} vencido{summary.overdue === 1 ? '' : 's'}
        </Link>
      )}
      <Link
        to="/my-fines"
        className={`summary__item ${summary.unpaid > 0 ? 'summary__item--danger' : ''}`}
      >
        💸 ${summary.unpaid.toLocaleString('es')} en multas
      </Link>
    </div>
  );
}

function CatalogForm({ onCataloged }) {
  const [title, setTitle] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const toast = useToast();

  async function handleSubmit(e) {
    e.preventDefault();
    setSubmitting(true);
    try {
      const book = await catalog.searchAndCatalog(title.trim());
      toast(`«${book.title}» catalogado.`);
      setTitle('');
      onCataloged();
    } catch (err) {
      toast(err.message, 'danger');
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
    </form>
  );
}

export function CatalogPage() {
  const { isStaff, isClient } = useAuth();
  const toast = useToast();
  // Búsqueda con debounce: se consulta al dejar de tipear, no en cada tecla.
  const [query, setQuery] = useState('');
  const [search, setSearch] = useState('');

  useEffect(() => {
    const t = setTimeout(() => setSearch(query), 300);
    return () => clearTimeout(t);
  }, [query]);

  const { data, loading, error, page, setPage, reload } = usePagedData(
    (p) => catalog.listBooks(p, 10, search),
    [search],
  );

  async function reservar(book) {
    try {
      await reservationsApi.reserve(book.id);
      toast(`Reservaste «${book.title}».`);
    } catch (err) {
      toast(err.message, 'danger');
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Catálogo</h1>
        <StatsBar />
      </header>

      {isClient && <ClientSummary />}
      {isStaff && <CatalogForm onCataloged={reload} />}

      <div className="field field--inline catalog-search">
        <label htmlFor="catalog-search">Buscar en el catálogo</label>
        <input
          id="catalog-search"
          type="search"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Filtrar por título…"
        />
      </div>

      <DataState
        loading={loading}
        error={error}
        empty={data?.content.length === 0}
        emptyMessage={
          search
            ? `No hay títulos que coincidan con «${search}».`
            : 'El catálogo está vacío. Catalogá el primer título desde Gutendex.'
        }
        onRetry={reload}
      >
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Título</th>
                <th>Autor</th>
                <th>Idioma</th>
                <th>Descargas</th>
                <th>Disponibles</th>
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
                  <td>{book.title}</td>
                  <td>{book.author.name}</td>
                  <td>{book.languages.join(', ')}</td>
                  <td>{book.downloadCount.toLocaleString('es')}</td>
                  <td>
                    <span className={`badge badge--${book.availableCopies > 0 ? 'ok' : 'muted'}`}>
                      {book.availableCopies > 0
                        ? `${book.availableCopies} disponible${book.availableCopies === 1 ? '' : 's'}`
                        : 'Sin ejemplares'}
                    </span>
                  </td>
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
