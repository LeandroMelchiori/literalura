import { BrowserRouter, Navigate, NavLink, Route, Routes, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { CatalogPage } from './pages/CatalogPage';
import { CopiesPage } from './pages/CopiesPage';
import { LoansPage } from './pages/LoansPage';
import { LoginPage } from './pages/LoginPage';
import { MembersPage } from './pages/MembersPage';
import { UsersPage } from './pages/UsersPage';

/** Exige autenticación y, opcionalmente, un rol; si falta, redirige. */
function RequireAuth({ children, requireAdmin = false }) {
  const { authenticated, isAdmin } = useAuth();
  const location = useLocation();
  if (!authenticated) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }
  if (requireAdmin && !isAdmin) {
    return <Navigate to="/" replace />;
  }
  return children;
}

function Header() {
  const { authenticated, isAdmin, role, logout } = useAuth();
  return (
    <header className="header">
      <div className="header__inner">
        <span className="header__brand">📚 LiteraLura</span>
        <nav className="header__nav" aria-label="Principal">
          <NavLink to="/">Catálogo</NavLink>
          {authenticated && (
            <>
              <NavLink to="/copies">Ejemplares</NavLink>
              <NavLink to="/members">Socios</NavLink>
              <NavLink to="/loans">Préstamos</NavLink>
              {isAdmin && <NavLink to="/users">Usuarios</NavLink>}
            </>
          )}
        </nav>
        {authenticated ? (
          <div className="header__user">
            <span className="badge badge--muted">
              {role === 'ADMIN' ? 'Administrador' : 'Bibliotecario'}
            </span>
            <button type="button" className="btn btn--secondary btn--small" onClick={logout}>
              Salir
            </button>
          </div>
        ) : (
          <NavLink to="/login" className="btn btn--small">
            Ingresar
          </NavLink>
        )}
      </div>
    </header>
  );
}

export function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Header />
        <main className="main">
          <Routes>
            <Route path="/" element={<CatalogPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route
              path="/copies"
              element={
                <RequireAuth>
                  <CopiesPage />
                </RequireAuth>
              }
            />
            <Route
              path="/members"
              element={
                <RequireAuth>
                  <MembersPage />
                </RequireAuth>
              }
            />
            <Route
              path="/loans"
              element={
                <RequireAuth>
                  <LoansPage />
                </RequireAuth>
              }
            />
            <Route
              path="/users"
              element={
                <RequireAuth requireAdmin>
                  <UsersPage />
                </RequireAuth>
              }
            />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </BrowserRouter>
    </AuthProvider>
  );
}
