import { render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { DataState } from './DataState';

describe('DataState', () => {
  it('muestra el estado de carga', () => {
    render(<DataState loading>contenido</DataState>);
    expect(screen.getByRole('status')).toHaveTextContent('Cargando');
  });

  it('muestra el mensaje de error y permite reintentar', () => {
    const onRetry = vi.fn();
    render(
      <DataState loading={false} error={new Error('Algo falló')} onRetry={onRetry}>
        contenido
      </DataState>,
    );
    expect(screen.getByRole('alert')).toHaveTextContent('Algo falló');
    expect(screen.getByRole('button', { name: 'Reintentar' })).toBeInTheDocument();
  });

  it('muestra el mensaje de vacío', () => {
    render(
      <DataState loading={false} empty emptyMessage="Sin datos">
        contenido
      </DataState>,
    );
    expect(screen.getByText('Sin datos')).toBeInTheDocument();
  });

  it('renderiza el contenido cuando hay datos', () => {
    render(
      <DataState loading={false} empty={false}>
        <p>contenido real</p>
      </DataState>,
    );
    expect(screen.getByText('contenido real')).toBeInTheDocument();
  });
});
