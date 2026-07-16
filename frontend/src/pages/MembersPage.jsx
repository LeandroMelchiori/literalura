import { useState } from 'react';
import * as library from '../api/library';
import { ConfirmDialog } from '../components/ConfirmDialog';
import { DataState } from '../components/DataState';
import { FormField } from '../components/FormField';
import { Pagination } from '../components/Pagination';
import { StatusBadge } from '../components/StatusBadge';
import { useToast } from '../context/ToastContext';
import { usePagedData } from '../hooks/usePagedData';

function RegisterMemberForm({ onRegistered }) {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [documentId, setDocumentId] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const toast = useToast();

  async function handleSubmit(e) {
    e.preventDefault();
    setSubmitting(true);
    try {
      const member = await library.registerMember({
        name: name.trim(),
        email: email.trim(),
        documentId: documentId.trim(),
        username: username.trim(),
        password,
      });
      toast(`Socio «${member.name}» dado de alta con acceso al portal.`);
      setName('');
      setEmail('');
      setDocumentId('');
      setUsername('');
      setPassword('');
      onRegistered();
    } catch (err) {
      toast(err.message, 'danger');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="card form-inline" onSubmit={handleSubmit}>
      <h2>Alta de socio</h2>
      <p className="muted">
        Se crea el socio y su acceso al portal. Validá la identidad de la persona
        antes de darle de alta.
      </p>
      <div className="form-inline__row">
        <FormField id="member-name" label="Nombre" value={name} onChange={setName} />
        <FormField id="member-email" label="Email" type="email" value={email} onChange={setEmail} />
        <FormField id="member-doc" label="Documento" value={documentId} onChange={setDocumentId} />
      </div>
      <div className="form-inline__row">
        <FormField id="member-user" label="Usuario (login)" value={username} onChange={setUsername} />
        <FormField
          id="member-pass"
          label="Contraseña (mín. 8)"
          type="password"
          value={password}
          onChange={setPassword}
          minLength={8}
        />
        <button type="submit" className="btn" disabled={submitting}>
          {submitting ? 'Guardando…' : 'Dar de alta'}
        </button>
      </div>
    </form>
  );
}

export function MembersPage() {
  const [search, setSearch] = useState('');
  const { data, loading, error, page, setPage, reload } = usePagedData(
    (p) => library.listMembers(p, 10, search),
    [search],
  );
  // Socio pendiente de confirmación de suspensión (null = diálogo cerrado).
  const [toSuspend, setToSuspend] = useState(null);
  const toast = useToast();

  async function changeStatus(member, next) {
    try {
      await library.changeMemberStatus(member.id, next);
      toast(next === 'SUSPENDED' ? `Socio «${member.name}» suspendido.` : `Socio «${member.name}» reactivado.`);
      reload();
    } catch (err) {
      toast(err.message, 'danger');
    }
  }

  function handleToggle(member) {
    if (member.status === 'ACTIVE') {
      setToSuspend(member); // Suspender es sensible: pide confirmación.
    } else {
      changeStatus(member, 'ACTIVE');
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Socios</h1>
        <div className="field field--inline">
          <label htmlFor="member-search">Buscar</label>
          <input
            id="member-search"
            type="search"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Por nombre…"
          />
        </div>
      </header>

      <RegisterMemberForm onRegistered={reload} />

      <DataState
        loading={loading}
        error={error}
        empty={data?.content.length === 0}
        emptyMessage={search ? `No hay socios que coincidan con «${search}».` : 'Todavía no hay socios registrados.'}
        emptyAction={
          !search && (
            <button
              type="button"
              className="btn"
              onClick={() => document.getElementById('member-name')?.focus()}
            >
              Dar de alta al primero
            </button>
          )
        }
        onRetry={reload}
      >
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Nombre</th>
                <th>Email</th>
                <th>Documento</th>
                <th>Estado</th>
                <th>
                  <span className="visually-hidden">Acciones</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {data?.content.map((member) => (
                <tr key={member.id}>
                  <td>{member.id}</td>
                  <td>{member.name}</td>
                  <td>{member.email}</td>
                  <td>{member.documentId}</td>
                  <td>
                    <StatusBadge status={member.status} />
                  </td>
                  <td>
                    <button
                      type="button"
                      className="btn btn--secondary btn--small"
                      onClick={() => handleToggle(member)}
                    >
                      {member.status === 'ACTIVE' ? 'Suspender' : 'Reactivar'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <Pagination page={page} totalPages={data?.totalPages ?? 0} onChange={setPage} />
      </DataState>

      <ConfirmDialog
        open={toSuspend !== null}
        title="Suspender socio"
        message={`¿Suspender a «${toSuspend?.name}»? No podrá pedir préstamos ni reservar hasta que se lo reactive.`}
        confirmLabel="Suspender"
        onConfirm={() => {
          changeStatus(toSuspend, 'SUSPENDED');
          setToSuspend(null);
        }}
        onCancel={() => setToSuspend(null)}
      />
    </section>
  );
}
