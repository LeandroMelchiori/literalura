import { createContext, useContext, useMemo, useState } from 'react';
import * as authApi from '../api/auth';
import { clearToken, getToken, setToken } from '../api/client';

const AuthContext = createContext(null);

/** Lee el claim `role` del JWT sin verificar la firma (eso lo hace el backend). */
function roleFromToken(token) {
  if (!token) return null;
  try {
    const payload = token.split('.')[1];
    const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(json).role ?? null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [role, setRole] = useState(() => roleFromToken(getToken()));
  const authenticated = role !== null;

  const value = useMemo(
    () => ({
      authenticated,
      role,
      isAdmin: role === 'ADMIN',
      async login(username, password) {
        const { token } = await authApi.login(username, password);
        setToken(token);
        setRole(roleFromToken(token));
      },
      logout() {
        clearToken();
        setRole(null);
      },
    }),
    [authenticated, role],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth debe usarse dentro de <AuthProvider>');
  return context;
}
