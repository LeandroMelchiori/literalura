import { createContext, useCallback, useContext, useRef, useState } from 'react';

const ToastContext = createContext(null);

let nextId = 0;

/** Notificaciones efímeras de resultado de acciones (se descartan solas). */
export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const timers = useRef({});

  const dismiss = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
    clearTimeout(timers.current[id]);
    delete timers.current[id];
  }, []);

  const addToast = useCallback(
    (text, tone = 'ok') => {
      const id = ++nextId;
      setToasts((prev) => [...prev, { id, text, tone }]);
      timers.current[id] = setTimeout(() => dismiss(id), 4500);
    },
    [dismiss],
  );

  return (
    <ToastContext.Provider value={addToast}>
      {children}
      <div className="toasts" role="status" aria-live="polite">
        {toasts.map((t) => (
          <div key={t.id} className={`toast toast--${t.tone}`}>
            <span>{t.text}</span>
            <button
              type="button"
              className="toast__close"
              aria-label="Cerrar notificación"
              onClick={() => dismiss(t.id)}
            >
              ×
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) throw new Error('useToast debe usarse dentro de <ToastProvider>');
  return context;
}
