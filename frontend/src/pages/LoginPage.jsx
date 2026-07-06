import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { FormField } from '../components/FormField';
import { useAuth } from '../context/AuthContext';

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await login(username, password);
      // Vuelve a la página que exigió login, o al inicio.
      navigate(location.state?.from ?? '/', { replace: true });
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="login">
      <form className="card login__card" onSubmit={handleSubmit}>
        <h1>Ingreso del personal</h1>
        <p className="muted">Acceso para bibliotecarios y administración.</p>
        <FormField
          id="username"
          label="Usuario"
          value={username}
          onChange={setUsername}
          autoComplete="username"
        />
        <FormField
          id="password"
          label="Contraseña"
          type="password"
          value={password}
          onChange={setPassword}
          autoComplete="current-password"
        />
        {error && (
          <p className="form-error" role="alert">
            {error.message}
          </p>
        )}
        <button type="submit" className="btn" disabled={submitting}>
          {submitting ? 'Ingresando…' : 'Ingresar'}
        </button>
      </form>
    </section>
  );
}
