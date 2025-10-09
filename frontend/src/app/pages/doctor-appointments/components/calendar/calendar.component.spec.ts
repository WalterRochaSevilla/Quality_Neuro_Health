import { CalendarComponent } from './calendar.component';

describe('CalendarComponent (Lógica TypeScript pura)', () => {
  let component: CalendarComponent;

  beforeEach(() => {
    component = new CalendarComponent();
    component.currentDate = new Date(2024, 4, 15); // Mayo 2024
  });

  // ==========================================================================
  // REGIÓN 1: GENERACIÓN DE CALENDARIO
  // ==========================================================================
  describe('generateCalendar()', () => {
    beforeEach(() => {
      component.ngOnChanges();
    });

    it('Debería generar una cuadrícula de 6x7', () => {
      expect(component.calendarDays.length).toBe(6);
      component.calendarDays.forEach(week => expect(week.length).toBe(7));
    });

    it('Debería comenzar en el lunes anterior al primer día del mes', () => {
      const firstDay = component.calendarDays[0][0];
      expect(firstDay?.date.getDate()).toBe(29); // 29 Abril 2024
      expect(firstDay?.isCurrentMonth).toBe(false);
    });

    it('Cada día debería tener estructura CalendarDay válida', () => {
      const sample = component.calendarDays[2][3];
      expect(sample).toHaveProperty('date');
      expect(sample).toHaveProperty('dayNumber');
      expect(sample).toHaveProperty('isCurrentMonth');
      expect(sample).toHaveProperty('hasAppointments');
      expect(sample).toHaveProperty('isToday');
    });

    it('Debería marcar 31 días como del mes actual', () => {
      const currentMonthDays = component.calendarDays.flat().filter(d => d?.isCurrentMonth);
      expect(currentMonthDays.length).toBe(31);
    });

    it('Debería marcar correctamente los días con citas', () => {
      component.appointmentsMap = new Map([
        ['2024-05-15', [{ id: 1, time: '10:00' }]],
      ]);
      component.ngOnChanges();
      const day15 = component.calendarDays.flat().find(d => d?.date.getDate() === 15);
      expect(day15?.hasAppointments).toBe(true);
    });

    it('Debería dejar hasAppointments=false sin citas', () => {
      component.appointmentsMap = new Map();
      component.ngOnChanges();
      const hasAny = component.calendarDays.flat().some(d => d?.hasAppointments);
      expect(hasAny).toBe(false);
    });

    it('Debería marcar correctamente el día actual', () => {
      vi.useFakeTimers();
      vi.setSystemTime(new Date(2024, 4, 15));
      component.ngOnChanges();
      const today = component.calendarDays.flat().find(d => d?.date.getDate() === 15);
      expect(today?.isToday).toBe(true);
      vi.useRealTimers();
    });

    it('Debería manejar cambio de año correctamente (Diciembre)', () => {
      component.currentDate = new Date(2023, 11, 25);
      component.ngOnChanges();
      const decemberDays = component.calendarDays.flat().filter(d => d?.isCurrentMonth);
      expect(decemberDays.length).toBe(31);
    });
  });

  // ==========================================================================
  // REGIÓN 2: isSelected()
  // ==========================================================================
  describe('isSelected()', () => {
    it('Retorna true para misma fecha', () => {
      const date = new Date(2024, 4, 15);
      component.selectedDate = new Date(2024, 4, 15);
      expect(component.isSelected(date)).toBe(true);
    });

    it('Retorna false cuando selectedDate es null', () => {
      const date = new Date(2024, 4, 15);
      component.selectedDate = null;
      expect(component.isSelected(date)).toBe(false);
    });

    it('Retorna false cuando date es null', () => {
      component.selectedDate = new Date(2024, 4, 15);
      expect(component.isSelected(null)).toBe(false);
    });

    it('Retorna false para fechas diferentes', () => {
      const date = new Date(2024, 4, 15);
      component.selectedDate = new Date(2024, 4, 16);
      expect(component.isSelected(date)).toBe(false);
    });
  });

  // ==========================================================================
  // REGIÓN 3: isToday()
  // ==========================================================================
  describe('isToday()', () => {
    beforeEach(() => {
      vi.useFakeTimers();
      vi.setSystemTime(new Date(2024, 4, 15));
    });
    afterEach(() => {
      vi.useRealTimers();
    });

    it('Retorna true si la fecha es hoy', () => {
      expect(component.isToday(new Date(2024, 4, 15))).toBe(true);
    });

    it('Retorna false si no es hoy', () => {
      expect(component.isToday(new Date(2024, 4, 16))).toBe(false);
    });

    it('Retorna false si date es null', () => {
      expect(component.isToday(null)).toBe(false);
    });
  });

  // ==========================================================================
  // REGIÓN 4: Eventos
  // ==========================================================================
  describe('Event Emitters', () => {
    it('Emite dateSelected al seleccionar una fecha', () => {
      const spy = vi.spyOn(component.dateSelected, 'emit');
      const date = new Date(2024, 4, 15);
      component.onDateSelect(date);
      expect(spy).toHaveBeenCalledWith(date);
    });

    it('Emite prevMonth al retroceder', () => {
      const spy = vi.spyOn(component.prevMonth, 'emit');
      component.onPrevMonth();
      expect(spy).toHaveBeenCalled();
    });

    it('Emite nextMonth al avanzar', () => {
      const spy = vi.spyOn(component.nextMonth, 'emit');
      component.onNextMonth();
      expect(spy).toHaveBeenCalled();
    });
  });
});
