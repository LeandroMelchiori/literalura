import { createContext, useContext, useMemo, useState } from 'react';
import * as authApi from '../api/auth';
import { clearToken, getToken, setToken } from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [authenticated, setAuthenticated] = useState(() => Boolean(getToken()));

  const value = useMemo(
    () => ({
      authenticated,
      async login(username, password) {
        const { token } = await authApi.login(username, password);
        setToken(token);
        setAuthenticated(true);
      },
      logout() {
        clearToken();
        setAuthenticated(false);
      },
    }),
    [authenticated],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth debe usarse dentro de <AuthProvider>');
  return context;
}
