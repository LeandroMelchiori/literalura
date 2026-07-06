export function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null;
  return (
    <nav className="pagination" aria-label="Paginación">
      <button
        type="button"
        className="btn btn--secondary"
        disabled={page === 0}
        onClick={() => onChange(page - 1)}
      >
        ← Anterior
      </button>
      <span aria-current="page">
        Página {page + 1} de {totalPages}
      </span>
      <button
        type="button"
        className="btn btn--secondary"
        disabled={page + 1 >= totalPages}
        onClick={() => onChange(page + 1)}
      >
        Siguiente →
      </button>
    </nav>
  );
}
