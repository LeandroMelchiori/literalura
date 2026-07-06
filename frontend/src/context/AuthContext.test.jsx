import { act, render, screen } from '@testing-library/react';
import { afterEach, describe, expect, it } from 'vitest';
import { AuthProvider, useAuth } from './AuthContext';

// Arma un JWT de mentira (solo el payload importa; la firma no se verifica en el cliente).
function fakeJwt(payload) {
  const b64 = (o) => btoa(JSON.stringify(o)).replace(/\+/g, '-').replace(/\//g, '_');
  return `${b64({ alg: 'HS384' })}.${b64(payload)}.sig`;
}

function Probe() {
  const { authenticated, role, isAdmin } = useAuth();
  return (
    <div>
      <span data-testid="auth">{String(authenticated)}</span>
      <span data-testid="role">{role ?? 'none'}</span>
      <span data-testid="admin">{String(isAdmin)}</span>
    </div>
  );
}

afterEach(() => localStorage.clear());

describe('AuthContext', () => {
  it('sin token, no está autenticado', () => {
    render(
      <AuthProvider>
        <Probe />
      </AuthProvider>,
    );
    expect(screen.getByTestId('auth')).toHaveTextContent('false');
    expect(screen.getByTestId('role')).toHaveTextContent('none');
  });

  it('lee el rol ADMIN del token guardado', () => {
    localStorage.setItem('literalura.token', fakeJwt({ sub: 'admin', role: 'ADMIN' }));
    render(
      <AuthProvider>
        <Probe />
      </AuthProvider>,
    );
    expect(screen.getByTestId('auth')).toHaveTextContent('true');
    expect(screen.getByTestId('role')).toHaveTextContent('ADMIN');
    expect(screen.getByTestId('admin')).toHaveTextContent('true');
  });

  it('un LIBRARIAN no es admin', () => {
    localStorage.setItem('literalura.token', fakeJwt({ sub: 'demo', role: 'LIBRARIAN' }));
    render(
      <AuthProvider>
        <Probe />
      </AuthProvider>,
    );
    expect(screen.getByTestId('role')).toHaveTextContent('LIBRARIAN');
    expect(screen.getByTestId('admin')).toHaveTextContent('false');
  });
});
