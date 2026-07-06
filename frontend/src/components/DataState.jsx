/**
 * Unifica los tres estados de toda vista con datos: cargando, error y vacío.
 * Si hay datos, renderiza children.
 */
export function DataState({ loading, error, empty, emptyMessage, onRetry, children }) {
  if (loading) {
    return (
      <div className="state" role="status" aria-live="polite">
        <span className="spinner" aria-hidden="true" />
        Cargando…
      </div>
    );
  }
  if (error) {
    return (
      <div className="state state--error" role="alert">
        <p>{error.message}</p>
        {onRetry && (
          <button type="button" className="btn btn--secondary" onClick={onRetry}>
            Reintentar
          </button>
        )}
      </div>
    );
  }
  if (empty) {
    return <div className="state">{emptyMessage ?? 'No hay datos todavía.'}</div>;
  }
  return children;
}
