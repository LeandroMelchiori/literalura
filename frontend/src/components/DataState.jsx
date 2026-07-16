/**
 * Unifica los tres estados de toda vista con datos: cargando (skeleton),
 * error y vacío (con acción opcional). Si hay datos, renderiza children.
 */
export function DataState({ loading, error, empty, emptyMessage, emptyAction, onRetry, children }) {
  if (loading) {
    return (
      <div className="state" role="status" aria-live="polite">
        <span className="visually-hidden">Cargando…</span>
        <div className="skeleton" aria-hidden="true">
          <div className="skeleton__bar" />
          <div className="skeleton__bar" />
          <div className="skeleton__bar" />
        </div>
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
    return (
      <div className="state">
        <p>{emptyMessage ?? 'No hay datos todavía.'}</p>
        {emptyAction}
      </div>
    );
  }
  return children;
}
