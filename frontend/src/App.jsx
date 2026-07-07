import { BrowserRouter, Navigate, NavLink, Route, Routes, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { CatalogPage } from './pages/CatalogPage';
import { CopiesPage } from './pages/CopiesPage';
import { LoansPage } from './pages/LoansPage';
import { LoginPage } from './pages/LoginPage';
import { MembersPage } from './pages/MembersPage';
import { MyLoansPage } from './pages/MyLoansPage';
import { MyReservationsPage } from './pages/MyReservationsPage';
import { ReservationsPage } from './pages/ReservationsPage';
import { UsersPage } from './pages/UsersPage';

/** Exige autenticación y, opcionalmente, un rol concreto; si falta, redirige. */
function RequireAuth({ children, allow }) {
  const { authenticated, role } = useAuth();
  const location = useLocation();
  if (!authenticated) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }
  if (allow && !allow.includes(role)) {
    return <Navigate to="/" replace />;
  }
  return children;
}

const STAFF = ['LIBRARIAN', 'ADMIN'];

function roleLabel(role) {
  return { ADMIN: 'Administrador', LIBRARIAN: 'Bibliotecario', CLIENTE: 'Socio' }[role] ?? role;
}

function Header() {
  const { authenticated, isAdmin, isStaff, isClient, role, logout } = useAuth();
  return (
    <header className="header">
      <div className="header__inner">
        <span className="header__brand">📚 LiteraLura</span>
        <nav className="header__nav" aria-label="Principal">
          <NavLink to="/">Catálogo</NavLink>
          {isStaff && (
            <>
              <NavLink to="/copies">Ejemplares</NavLink>
              <NavLink to="/members">Socios</NavLink>
              <NavLink to="/loans">Préstamos</NavLink>
              <NavLink to="/reservations">Reservas</NavLink>
              {isAdmin && <NavLink to="/users">Usuarios</NavLink>}
            </>
          )}
          {isClient && (
            <>
              <NavLink to="/my-loans">Mis préstamos</NavLink>
              <NavLink to="/my-reservations">Mis reservas</NavLink>
            </>
          )}
        </nav>
        {authenticated ? (
          <div className="header__user">
            <span className="badge badge--muted">{roleLabel(role)}</span>
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

            {/* Personal */}
            <Route path="/copies" element={<RequireAuth allow={STAFF}><CopiesPage /></RequireAuth>} />
            <Route path="/members" element={<RequireAuth allow={STAFF}><MembersPage /></RequireAuth>} />
            <Route path="/loans" element={<RequireAuth allow={STAFF}><LoansPage /></RequireAuth>} />
            <Route path="/reservations" element={<RequireAuth allow={STAFF}><ReservationsPage /></RequireAuth>} />
            <Route path="/users" element={<RequireAuth allow={['ADMIN']}><UsersPage /></RequireAuth>} />

            {/* Cliente */}
            <Route path="/my-loans" element={<RequireAuth allow={['CLIENTE']}><MyLoansPage /></RequireAuth>} />
            <Route path="/my-reservations" element={<RequireAuth allow={['CLIENTE']}><MyReservationsPage /></RequireAuth>} />

            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </BrowserRouter>
    </AuthProvider>
  );
}
