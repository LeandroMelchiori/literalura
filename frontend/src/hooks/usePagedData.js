import { useCallback, useEffect, useState } from 'react';

/**
 * Trae una página de datos y expone los tres estados (cargando/error/datos)
 * más el control de paginación. `fetcher(page)` debe devolver un Page de Spring.
 */
export function usePagedData(fetcher, deps = []) {
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setData(await fetcher(page));
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, ...deps]);

  useEffect(() => {
    load();
  }, [load]);

  // Al cambiar un filtro (deps) se vuelve a la primera página.
  useEffect(() => {
    setPage(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  return { data, loading, error, page, setPage, reload: load };
}
