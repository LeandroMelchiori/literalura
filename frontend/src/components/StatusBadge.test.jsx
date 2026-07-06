import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { StatusBadge } from './StatusBadge';

describe('StatusBadge', () => {
  it('traduce el estado del ejemplar a etiqueta legible', () => {
    render(<StatusBadge status="AVAILABLE" />);
    expect(screen.getByText('Disponible')).toBeInTheDocument();
  });

  it('muestra un préstamo activo pero vencido como "Vencido"', () => {
    render(<StatusBadge status="ACTIVE" overdue />);
    expect(screen.getByText('Vencido')).toBeInTheDocument();
  });

  it('un préstamo activo al día se muestra como "Activo"', () => {
    render(<StatusBadge status="ACTIVE" />);
    expect(screen.getByText('Activo')).toBeInTheDocument();
  });
});
