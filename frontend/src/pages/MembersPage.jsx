import { useState } from 'react';
import * as library from '../api/library';
import { DataState } from '../components/DataState';
import { FormField } from '../components/FormField';
import { Pagination } from '../components/Pagination';
import { StatusBadge } from '../components/StatusBadge';
import { usePagedData } from '../hooks/usePagedData';

function RegisterMemberForm({ onRegistered }) {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [documentId, setDocumentId] = useState('');
  const [feedback, setFeedback] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setFeedback(null);
    setSubmitting(true);
    try {
      const member = await library.registerMember({
        name: name.trim(),
        email: email.trim(),
        documentId: documentId.trim(),
      });
      setFeedback({ ok: true, text: `Socio «${member.name}» dado de alta.` });
      setName('');
      setEmail('');
      setDocumentId('');
      onRegistered();
    } catch (err) {
      setFeedback({ ok: false, text: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="card form-inline" onSubmit={handleSubmit}>
      <h2>Alta de socio</h2>
      <div className="form-inline__row">
        <FormField id="member-name" label="Nombre" value={name} onChange={setName} />
        <FormField id="member-email" label="Email" type="email" value={email} onChange={setEmail} />
        <FormField id="member-doc" label="Documento" value={documentId} onChange={setDocumentId} />
        <button type="submit" className="btn" disabled={submitting}>
          {submitting ? 'Guardando…' : 'Dar de alta'}
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

export function MembersPage() {
  const { data, loading, error, page, setPage, reload } = usePagedData(
    (p) => library.listMembers(p),
  );
  const [actionError, setActionError] = useState(null);

  async function toggleStatus(member) {
    setActionError(null);
    const next = member.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE';
    try {
      await library.changeMemberStatus(member.id, next);
      reload();
    } catch (err) {
      setActionError(err);
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Socios</h1>
      </header>

      <RegisterMemberForm onRegistered={reload} />

      {actionError && (
        <p className="form-error" role="alert">
          {actionError.message}
        </p>
      )}

      <DataState
        loading={loading}
        error={error}
        empty={data?.content.length === 0}
        emptyMessage="Todavía no hay socios registrados."
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
                      onClick={() => toggleStatus(member)}
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
    </section>
  );
}
