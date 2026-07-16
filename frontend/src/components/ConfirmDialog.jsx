import { useEffect, useRef } from 'react';

/** Confirmación previa a acciones sensibles (suspender, cancelar). */
export function ConfirmDialog({ open, title, message, confirmLabel = 'Confirmar', onConfirm, onCancel }) {
  const confirmRef = useRef(null);

  // Foco al abrir y cierre con Escape, para uso con teclado.
  useEffect(() => {
    if (!open) return undefined;
    confirmRef.current?.focus();
    const onKey = (e) => e.key === 'Escape' && onCancel();
    document.addEventListener('keydown', onKey);
    return () => document.removeEventListener('keydown', onKey);
  }, [open, onCancel]);

  if (!open) return null;
  return (
    <div className="dialog-overlay" onClick={onCancel}>
      <div
        className="dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby="dialog-title"
        onClick={(e) => e.stopPropagation()}
      >
        <h2 id="dialog-title">{title}</h2>
        <p>{message}</p>
        <div className="dialog__actions">
          <button type="button" className="btn btn--secondary" onClick={onCancel}>
            Volver
          </button>
          <button type="button" className="btn" ref={confirmRef} onClick={onConfirm}>
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}
