import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { daysUntil, dueLabel, formatDate } from './dates';

describe('dates', () => {
  beforeEach(() => {
    // Fija "hoy" para que los cálculos relativos sean deterministas.
    vi.useFakeTimers();
    vi.setSystemTime(new Date(2026, 6, 7)); // 7 jul 2026 (mes 0-indexado)
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('formatea la fecha ISO sin corrimiento de día', () => {
    // new Date('2026-07-21') la tomaría como UTC; el parseo manual lo evita.
    expect(formatDate('2026-07-21')).toMatch(/21.*jul.*2026/i);
  });

  it('muestra guión cuando no hay fecha', () => {
    expect(formatDate(null)).toBe('—');
  });

  it('calcula los días hasta una fecha', () => {
    expect(daysUntil('2026-07-10')).toBe(3);
    expect(daysUntil('2026-07-07')).toBe(0);
    expect(daysUntil('2026-07-05')).toBe(-2);
  });

  it('describe el vencimiento en lenguaje natural', () => {
    expect(dueLabel('2026-07-07')).toBe('vence hoy');
    expect(dueLabel('2026-07-08')).toBe('vence mañana');
    expect(dueLabel('2026-07-10')).toBe('vence en 3 días');
    expect(dueLabel('2026-07-06')).toBe('venció ayer');
    expect(dueLabel('2026-07-04')).toBe('venció hace 3 días');
  });
});
