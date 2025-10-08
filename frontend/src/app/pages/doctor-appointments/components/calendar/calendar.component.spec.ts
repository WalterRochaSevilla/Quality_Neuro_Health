import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CalendarComponent, CalendarDay } from './calendar.component';
import { DatePipe } from '@angular/common';

describe('CalendarComponent - Pruebas Complejas', () => {
  let component: CalendarComponent;
  let fixture: ComponentFixture<CalendarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalendarComponent],
      providers: [DatePipe]
    }).compileComponents();

    fixture = TestBed.createComponent(CalendarComponent);
    component = fixture.componentInstance;
    
    component.currentDate = new Date(2024, 4, 15); // Mayo 15, 2024
    fixture.detectChanges();
  });

  describe('Método generateCalendar() - Lógica Compleja', () => {
    it('debería generar una estructura de calendario válida con 6 semanas', () => {
      component.ngOnChanges();
      
      expect(component.calendarDays.length).toBe(6);
      component.calendarDays.forEach(week => {
        expect(week.length).toBe(7);
      });
    });

    it('debería empezar el calendario en el lunes correcto para el mes', () => {
      component.ngOnChanges();
      
      const firstDay = component.calendarDays[0][0];
      expect(firstDay?.date.getDate()).toBe(29); // 29 de Abril
      expect(firstDay?.date.getMonth()).toBe(3); // Abril
      expect(firstDay?.isCurrentMonth).toBe(false);
    });

    it('debería terminar el calendario en el domingo correcto', () => {
      component.ngOnChanges();
      
      const lastWeek = component.calendarDays[5];
      const lastDay = lastWeek[6];
      expect(lastDay?.date.getDate()).toBe(9); // 9 de Junio
      expect(lastDay?.date.getMonth()).toBe(5); // Junio
      expect(lastDay?.isCurrentMonth).toBe(false);
    });

    it('debería marcar correctamente los días del mes actual', () => {
      component.ngOnChanges();
      
      const allDays = component.calendarDays
        .reduce((acc, week) => acc.concat(week), [] as (CalendarDay | null)[])
        .filter(day => day !== null);
      
      const currentMonthDays = allDays.filter(day => day.isCurrentMonth);
      
      expect(currentMonthDays.length).toBe(31); // Mayo tiene 31 días
      
      currentMonthDays.forEach(day => {
        expect(day.date.getMonth()).toBe(4); // Mayo
      });
    });

    it('debería manejar correctamente el cambio de año', () => {
      component.currentDate = new Date(2024, 11, 31); // 31 Diciembre 2024
      component.ngOnChanges();
      
      const firstDay = component.calendarDays[0][0];
      // Diciembre 2024 empieza en domingo, pero el calendario empieza en lunes (25 Noviembre)
      expect(firstDay?.date.getDate()).toBe(25);
      expect(firstDay?.date.getMonth()).toBe(10); // Noviembre
      expect(firstDay?.date.getFullYear()).toBe(2024);
      
      const lastDay = component.calendarDays[5][6];
      expect(lastDay?.date.getDate()).toBe(5);
      expect(lastDay?.date.getMonth()).toBe(0); // Enero 2025
      expect(lastDay?.date.getFullYear()).toBe(2025);
    });

    it('debería mostrar indicadores de citas correctamente', () => {
      const appointmentsMap = new Map();
      appointmentsMap.set('2024-05-15', [{ id: '1', hora: '10:00' }]);
      appointmentsMap.set('2024-05-20', [{ id: '2', hora: '11:00' }]);
      
      component.appointmentsMap = appointmentsMap;
      component.ngOnChanges();
      
      const allDays = component.calendarDays
        .reduce((acc, week) => acc.concat(week), [] as (CalendarDay | null)[])
        .filter(day => day !== null);
      
      const daysWithAppointments = allDays.filter(day => day.hasAppointments);
      
      expect(daysWithAppointments.length).toBe(2);
      
      const may15 = allDays.find(day => 
        day.date.getDate() === 15 && 
        day.date.getMonth() === 4 && 
        day.date.getFullYear() === 2024
      );
      
      expect(may15?.hasAppointments).toBe(true);
    });
  });

  describe('Métodos de Utilidad - Lógica Condicional', () => {
    describe('isSelected()', () => {
      it('debería retornar true cuando la fecha coincide con selectedDate', () => {
        const testDate = new Date(2024, 4, 15);
        component.selectedDate = new Date(2024, 4, 15);
        
        const result = component.isSelected(testDate);
        
        expect(result).toBe(true);
      });

      it('debería retornar false cuando selectedDate es null', () => {
        const testDate = new Date(2024, 4, 15);
        component.selectedDate = null;
        
        const result = component.isSelected(testDate);
        
        expect(result).toBe(false);
      });

      it('debería retornar false cuando la fecha es null', () => {
        component.selectedDate = new Date(2024, 4, 15);
        
        const result = component.isSelected(null);
        
        expect(result).toBe(false);
      });

      it('debería retornar false cuando las fechas no coinciden', () => {
        const testDate = new Date(2024, 4, 15);
        component.selectedDate = new Date(2024, 4, 16);
        
        const result = component.isSelected(testDate);
        
        expect(result).toBe(false);
      });

      it('debería ignorar la hora al comparar fechas', () => {
        const testDate = new Date(2024, 4, 15, 10, 30); // 10:30 AM
        component.selectedDate = new Date(2024, 4, 15, 14, 45); // 2:45 PM
        
        const result = component.isSelected(testDate);
        
        // Debería ser true porque solo importa la fecha (día, mes, año)
        expect(result).toBe(true);
      });
    });

    describe('isToday()', () => {
      it('debería identificar correctamente el día de hoy', () => {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        const result = component.isToday(today);
        
        expect(result).toBe(true);
      });

      it('debería retornar false para días que no son hoy', () => {
        const notToday = new Date(2023, 0, 1);
        
        const result = component.isToday(notToday);
        
        expect(result).toBe(false);
      });

      it('debería retornar false para fecha null', () => {
        const result = component.isToday(null);
        
        expect(result).toBe(false);
      });

      it('debería ser insensible a la hora (solo compara fecha)', () => {
        const today = new Date();
        const todayMorning = new Date(today);
        todayMorning.setHours(10, 30, 0, 0);
        
        const todayEvening = new Date(today);
        todayEvening.setHours(22, 45, 0, 0);
        
        expect(component.isToday(todayMorning)).toBe(true);
        expect(component.isToday(todayEvening)).toBe(true);
      });
    });

    describe('formatDate() - Método Privado', () => {
      it('debería formatear fechas correctamente a YYYY-MM-DD', () => {
        const testDate = new Date(2024, 4, 15);
        
        const formatted = (component as any).formatDate(testDate);
        
        expect(formatted).toBe('2024-05-15');
      });

      it('debería manejar meses y días de un dígito', () => {
        const testDate = new Date(2024, 0, 5);
        
        const formatted = (component as any).formatDate(testDate);
        
        expect(formatted).toBe('2024-01-05');
      });

      it('debería manejar correctamente el cambio de año', () => {
        const testDate = new Date(2023, 11, 31);
        
        const formatted = (component as any).formatDate(testDate);
        
        expect(formatted).toBe('2023-12-31');
      });
    });
  });

  describe('Event Emitters - Comportamiento de Salida', () => {
    it('debería emitir dateSelected con la fecha correcta', () => {
      const testDate = new Date(2024, 4, 15);
      let emittedDate: Date | undefined;
      
      component.dateSelected.subscribe(date => {
        emittedDate = date;
      });
      
      component.onDateSelect(testDate);
      
      expect(emittedDate).toEqual(testDate);
    });

    it('debería emitir prevMonth cuando se navega al mes anterior', () => {
      let emissionCount = 0;
      
      component.prevMonth.subscribe(() => {
        emissionCount++;
      });
      
      component.onPrevMonth();
      
      expect(emissionCount).toBe(1);
    });

    it('debería emitir nextMonth cuando se navega al mes siguiente', () => {
      let emissionCount = 0;
      
      component.nextMonth.subscribe(() => {
        emissionCount++;
      });
      
      component.onNextMonth();
      
      expect(emissionCount).toBe(1);
    });

    it('debería regenerar el calendario cuando cambia currentDate', () => {
      const initialCalendar = component.calendarDays;
      
      component.currentDate = new Date(2024, 5, 1);
      component.ngOnChanges();
      
      const newCalendar = component.calendarDays;
      
      expect(newCalendar).not.toEqual(initialCalendar);
      
      const firstDayJune = newCalendar[0].find(day => 
        day?.isCurrentMonth && day.date.getDate() === 1
      );
      
      expect(firstDayJune).toBeTruthy();
      expect(firstDayJune?.date.getMonth()).toBe(5);
    });
  });

  describe('Pruebas de Integración - Comportamiento Complejo', () => {
    it('debería mantener la selección al cambiar de mes', () => {
      const selectedDate = new Date(2024, 4, 15);
      component.selectedDate = selectedDate;
      component.ngOnChanges();
      
      component.currentDate = new Date(2024, 5, 1);
      component.ngOnChanges();
      
      expect(component.selectedDate).toEqual(selectedDate);
    });

    it('debería actualizar los indicadores de citas cuando cambia appointmentsMap', () => {
      component.ngOnChanges();
      
      const initialDaysWithAppointments = component.calendarDays
        .reduce((acc, week) => acc.concat(week), [] as (CalendarDay | null)[])
        .filter(day => day !== null)
        .filter(day => day.hasAppointments)
        .length;
      
      const newAppointmentsMap = new Map();
      newAppointmentsMap.set('2024-05-10', [{ id: '1', hora: '09:00' }]);
      newAppointmentsMap.set('2024-05-11', [{ id: '2', hora: '10:00' }]);
      
      component.appointmentsMap = newAppointmentsMap;
      component.ngOnChanges();
      
      const updatedDaysWithAppointments = component.calendarDays
        .reduce((acc, week) => acc.concat(week), [] as (CalendarDay | null)[])
        .filter(day => day !== null)
        .filter(day => day.hasAppointments)
        .length;
      
      expect(updatedDaysWithAppointments).toBe(2);
      expect(updatedDaysWithAppointments).not.toBe(initialDaysWithAppointments);
    });

    it('debería manejar correctamente meses con 5 semanas en el calendario', () => {
      component.currentDate = new Date(2025, 1, 1);
      component.ngOnChanges();
      
      expect(component.calendarDays.length).toBe(6);
      
      const lastWeek = component.calendarDays[5];
      const marchDays = lastWeek.filter(day => day && day.date.getMonth() === 2);
      
      expect(marchDays.length).toBeGreaterThan(0);
    });
  });
});