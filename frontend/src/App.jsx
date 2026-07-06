import { BrowserRouter, Navigate, NavLink, Route, Routes, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { CatalogPage } from './pages/CatalogPage';
import { CopiesPage } from './pages/CopiesPage';
import { LoansPage } from './pages/LoansPage';
import { LoginPage } from './pages/LoginPage';
import { MembersPage } from './pages/MembersPage';

/** Redirige a login guardando de dónde venía, para volver después. */
function RequireAuth({ children }) {
  const { authenticated } = useAuth();
  const location = useLocation();
  if (!authenticated) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }
  return children;
}

function Header() {
  const { authenticated, logout } = useAuth();
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
            </>
          )}
        </nav>
        {authenticated ? (
          <button type="button" className="btn btn--secondary btn--small" onClick={logout}>
            Salir
          </button>
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
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </BrowserRouter>
    </AuthProvider>
  );
}
