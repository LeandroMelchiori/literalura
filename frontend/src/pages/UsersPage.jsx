import { useCallback, useEffect, useState } from 'react';
import * as authApi from '../api/auth';
import { DataState } from '../components/DataState';
import { FormField } from '../components/FormField';

function CreateUserForm({ onCreated }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('LIBRARIAN');
  const [feedback, setFeedback] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setFeedback(null);
    setSubmitting(true);
    try {
      const user = await authApi.createUser({ username: username.trim(), password, role });
      setFeedback({ ok: true, text: `Usuario «${user.username}» (${user.role}) creado.` });
      setUsername('');
      setPassword('');
      onCreated();
    } catch (err) {
      setFeedback({ ok: false, text: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="card form-inline" onSubmit={handleSubmit}>
      <h2>Crear usuario del personal</h2>
      <div className="form-inline__row">
        <FormField id="user-name" label="Usuario" value={username} onChange={setUsername} />
        <FormField
          id="user-pass"
          label="Contraseña (mín. 8)"
          type="password"
          value={password}
          onChange={setPassword}
          minLength={8}
        />
        <div className="field">
          <label htmlFor="user-role">Rol</label>
          <select id="user-role" value={role} onChange={(e) => setRole(e.target.value)}>
            <option value="LIBRARIAN">Bibliotecario</option>
            <option value="ADMIN">Administrador</option>
          </select>
        </div>
        <button type="submit" className="btn" disabled={submitting || !username.trim() || !password}>
          {submitting ? 'Creando…' : 'Crear'}
        </button>
      </div>
      {feedback && (
        <p className={feedback.ok ? 'form-ok' : 'form-error'} role="status">
          {feedback.text}
        </p>
      )}
    </form>
  );
}

export function UsersPage() {
  const [users, setUsers] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setUsers(await authApi.listUsers());
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <section>
      <header className="page-header">
        <h1>Usuarios del personal</h1>
      </header>

      <CreateUserForm onCreated={load} />

      <DataState
        loading={loading}
        error={error}
        empty={users?.length === 0}
        emptyMessage="No hay usuarios."
        onRetry={load}
      >
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Usuario</th>
                <th>Rol</th>
              </tr>
            </thead>
            <tbody>
              {users?.map((user) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.username}</td>
                  <td>
                    <span className={`badge badge--${user.role === 'ADMIN' ? 'warn' : 'muted'}`}>
                      {user.role === 'ADMIN' ? 'Administrador' : 'Bibliotecario'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </DataState>
    </section>
  );
}
