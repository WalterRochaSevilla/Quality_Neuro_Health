import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CitaService, Cita } from '../../services/cita.service';
import { AuthService } from '../../auth.service';

interface Appointment {
  id: string;
  fecha: string;
  hora: string;
  pacienteId?: string;
  pacienteNombre?: string;
  especialistaId: string;
  estado: 'confirmada' | 'pendiente_confirmacion_paciente' | 'pendiente_confirmacion_especialista' | 'cancelada_paciente' | 'cancelada_especialista' | 'reagendada';
}

interface CalendarDay {
  date: Date;
  dayNumber: number;
  isCurrentMonth: boolean;
  hasAppointments: boolean;
  isToday: boolean;
}

const MOCK_SPECIALIST_ID = '681043720e4c1b9eaf382310';

@Component({
  selector: 'app-doctor-appointments',
  standalone: true,
  imports: [CommonModule, RouterModule, DatePipe],
  templateUrl: './doctor-appointments.component.html',
  styleUrls: ['./doctor-appointments.component.css'],
  providers: [DatePipe]
})
export class DoctorAppointmentsComponent implements OnInit {
  currentSpecialistId: string | null = MOCK_SPECIALIST_ID;

  currentDate = new Date();
  calendarDays: (CalendarDay | null)[][] = [];

  selectedDate: Date | null = null;
  appointmentsForSelectedDate: Appointment[] = [];

  private allFetchedAppointments: Map<string, Appointment[]> = new Map();

  isLoadingAppointments = false;

  isRescheduling = false;
  appointmentToReschedule: Appointment | null = null;
  reschedulePromptMessage: string | null = null;

  constructor(
    private authService: AuthService,
    private datePipe: DatePipe,
    private citaService: CitaService
  ) {}

  ngOnInit(): void {
    this.currentSpecialistId = this.authService.getUsuario().id;
    if (this.currentSpecialistId) {
      this.loadAppointmentsForMonthRange(this.currentDate);
    } else {
      console.warn("No specialist ID configured. Appointment management disabled.");
    }
  }

  loadAppointmentsForMonthRange(dateForMonth: Date): void {
    if (!this.currentSpecialistId) return;

    this.isLoadingAppointments = true;
    this.allFetchedAppointments.clear();

    // Fetch appointments from the service
    this.citaService.obtenerCitasPorEspecialista(this.currentSpecialistId).subscribe({
      next: (citas) => {
        console.log('Citas obtenidas:', citas);
        citas.forEach(cita => {
          const appointment: Appointment = {
            id: cita.id || '',
            fecha: cita.fecha,
            hora: cita.hora,
            pacienteId: cita.usuarioId,
            pacienteNombre: cita.usuario,
            especialistaId: cita.especialistaId,
            estado: 'confirmada' // Default state, should come from backend
          };

          const dateKey = cita.fecha;

          if (!this.allFetchedAppointments.has(dateKey)) {
            this.allFetchedAppointments.set(dateKey, []);
          }
          this.allFetchedAppointments.get(dateKey)?.push(appointment);
        });

        this.generateCalendar(this.currentDate);
        if (this.selectedDate && !this.isRescheduling) {
          this.fetchAppointmentsForSelectedDate(this.selectedDate);
        }
        this.isLoadingAppointments = false;
      },
      error: (error) => {
        console.error('Error loading appointments:', error);
        this.isLoadingAppointments = false;
        alert('Error al cargar las citas. Por favor, inténtelo de nuevo más tarde.');
      }
    });
  }

  generateCalendar(date: Date): void {
    this.calendarDays = [];
    const year = date.getFullYear();
    const month = date.getMonth();
    const today = new Date();
    today.setHours(0,0,0,0);

    const firstDayOfMonth = new Date(year, month, 1);

    let startDayOfWeek = firstDayOfMonth.getDay();
    startDayOfWeek = startDayOfWeek === 0 ? 6 : startDayOfWeek - 1;

    let currentCalDate = new Date(firstDayOfMonth);
    currentCalDate.setDate(currentCalDate.getDate() - startDayOfWeek);

    const weeksToDisplay = 6;

    for (let i = 0; i < weeksToDisplay; i++) {
      const week: (CalendarDay | null)[] = [];
      for (let j = 0; j < 7; j++) {
        const dayDate = new Date(currentCalDate);
        dayDate.setHours(0,0,0,0);
        const dateKey = this.datePipe.transform(dayDate, 'yyyy-MM-dd')!;

        week.push({
          date: dayDate,
          dayNumber: dayDate.getDate(),
          isCurrentMonth: dayDate.getMonth() === month,
          hasAppointments: this.allFetchedAppointments.has(dateKey) && (this.allFetchedAppointments.get(dateKey)?.length || 0) > 0,
          isToday: dayDate.getTime() === today.getTime()
        });
        currentCalDate.setDate(currentCalDate.getDate() + 1);
      }
      this.calendarDays.push(week);
    }
  }

  isToday(date: Date | undefined | null): boolean {
    if (!date) return false;
    const today = new Date();
    return date.getDate() === today.getDate() &&
           date.getMonth() === today.getMonth() &&
           date.getFullYear() === today.getFullYear();
  }

  selectDate(date: Date | null | undefined): void {
    if (!date) {
      this.selectedDate = null;
      if (!this.isRescheduling) {
        this.appointmentsForSelectedDate = [];
      }
      return;
    }

    const newSelectedDate = new Date(date);
    newSelectedDate.setHours(0,0,0,0);

    if (newSelectedDate.getMonth() !== this.currentDate.getMonth() || newSelectedDate.getFullYear() !== this.currentDate.getFullYear()) {
        this.currentDate = new Date(newSelectedDate.getFullYear(), newSelectedDate.getMonth(), 1);
        this.generateCalendar(this.currentDate);
    }

    this.selectedDate = newSelectedDate;

    if (this.isRescheduling && this.appointmentToReschedule) {
      this.confirmRescheduleDate(newSelectedDate);
    } else {
      this.fetchAppointmentsForSelectedDate(this.selectedDate);
    }
  }

  fetchAppointmentsForSelectedDate(date: Date): void {
    const dateKey = this.datePipe.transform(date, 'yyyy-MM-dd')!;
    const appointments = this.allFetchedAppointments.get(dateKey) || [];
    this.appointmentsForSelectedDate = [...appointments].sort((a,b) => {
        return a.hora.localeCompare(b.hora);
    });
  }

  isSelected(date: Date | undefined | null): boolean {
    if (!date || !this.selectedDate) return false;
    return date.getTime() === this.selectedDate.getTime();
  }

  prevMonth(): void {
    this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() - 1, 1);
    this.generateCalendar(this.currentDate);
    this.loadAppointmentsForMonthRange(this.currentDate);
  }

  nextMonth(): void {
    this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, 1);
    this.generateCalendar(this.currentDate);
    this.loadAppointmentsForMonthRange(this.currentDate);
  }

  cancelAppointment(appointmentId: string): void {
    if (this.isRescheduling) {
        this.cancelRescheduleProcess(); 
        return;
    }
    if (!this.currentSpecialistId) return;

    if (confirm('¿Está seguro de que desea cancelar esta cita? Esta acción no se puede deshacer.')) {
      // Find appointment in local data first
      const appointment = this.findAppointmentById(appointmentId);
      
      if (appointment) {
        // In a real implementation, you would call a service method to cancel the appointment
        // For now, we'll just update our local data
        const originalDateKey = appointment.fecha;
        appointment.estado = 'cancelada_especialista';

        alert('Cita cancelada exitosamente.');

        // Update the UI to reflect the changes
        if (this.selectedDate && this.datePipe.transform(this.selectedDate, 'yyyy-MM-dd') === originalDateKey) {
          this.fetchAppointmentsForSelectedDate(this.selectedDate);
        }
        this.generateCalendar(this.currentDate);
      } else {
        alert('Error: Cita no encontrada o no pertenece a este especialista.');
      }
    }
  }

  findAppointmentById(appointmentId: string): Appointment | null {
    // Look through all appointments in the map
    for (const [_, appointments] of this.allFetchedAppointments.entries()) {
      const found = appointments.find(app => app.id === appointmentId);
      if (found) return found;
    }
    return null;
  }

  openRescheduleModal(appointment: Appointment): void {
    this.isRescheduling = true;
    this.appointmentToReschedule = {...appointment}; 
    this.appointmentsForSelectedDate = []; 
    this.selectedDate = null; 
    this.reschedulePromptMessage = `Reagendando cita para ${appointment.pacienteNombre}. Por favor, seleccione la NUEVA FECHA en el calendario.`;
    console.log('Iniciando reagendamiento para:', this.appointmentToReschedule);
  }

  confirmRescheduleDate(newDate: Date): void {
    if (!this.appointmentToReschedule) return;

    const newDateFormatted = this.datePipe.transform(newDate, 'yyyy-MM-dd')!;
    const oldDateFormatted = this.appointmentToReschedule.fecha;
    const newTime = this.appointmentToReschedule.hora; 

    if (confirm(`¿Reagendar cita de ${this.appointmentToReschedule.pacienteNombre} de ${oldDateFormatted} a ${newDateFormatted} a las ${newTime}?`)) {
      // In a real implementation, you would call a service method to reschedule the appointment
      // Something like:
      /*
      const updatedCita: Cita = {
        id: this.appointmentToReschedule.id,
        usuarioId: this.appointmentToReschedule.pacienteId || '',
        especialistaId: this.appointmentToReschedule.especialistaId,
        fecha: newDateFormatted,
        hora: newTime
      };
      
      this.citaService.actualizarCita(updatedCita).subscribe({
        next: (response) => {
          // Handle success
          this.loadAppointmentsForMonthRange(this.currentDate);
          alert(`Cita reagendada para ${this.appointmentToReschedule.pacienteNombre} a ${newDateFormatted} ${newTime}.`);
          this.exitRescheduleMode(newDate);
        },
        error: (error) => {
          // Handle error
          alert('Error al reagendar la cita. Por favor, inténtelo de nuevo más tarde.');
          console.error('Error rescheduling appointment:', error);
        }
      });
      */
      
      // For now, let's simulate success
      // Update the appointment in our local data
      const appointment = this.findAppointmentById(this.appointmentToReschedule.id);
      if (appointment) {
        // Remove from old date
        const oldDateKey = appointment.fecha;
        const oldAppointments = this.allFetchedAppointments.get(oldDateKey) || [];
        const updatedOldAppointments = oldAppointments.filter(a => a.id !== appointment.id);
        
        if (updatedOldAppointments.length > 0) {
          this.allFetchedAppointments.set(oldDateKey, updatedOldAppointments);
        } else {
          this.allFetchedAppointments.delete(oldDateKey);
        }
        
        // Add to new date
        appointment.fecha = newDateFormatted;
        appointment.estado = 'reagendada';
        
        if (!this.allFetchedAppointments.has(newDateFormatted)) {
          this.allFetchedAppointments.set(newDateFormatted, []);
        }
        this.allFetchedAppointments.get(newDateFormatted)?.push(appointment);
      }
      
      this.generateCalendar(this.currentDate);
      alert(`Cita reagendada para ${this.appointmentToReschedule.pacienteNombre} a ${newDateFormatted} ${newTime}.`);
      this.exitRescheduleMode(newDate); 
    } else {
      this.exitRescheduleMode(null); 
    }
  }

  cancelRescheduleProcess(): void {
    if (confirm("¿Cancelar el proceso de reagendamiento?")) {
        this.exitRescheduleMode(this.selectedDate); 
    }
  }

  private exitRescheduleMode(dateToSelectAfterExit: Date | null): void {
    this.isRescheduling = false;
    this.appointmentToReschedule = null;
    this.reschedulePromptMessage = null;
    this.selectedDate = null; 
    if(dateToSelectAfterExit) {
        this.selectDate(dateToSelectAfterExit); 
    } else {
        this.appointmentsForSelectedDate = []; 
    }
  }

  getEstadoText(estado: Appointment['estado']): string {
    switch (estado) {
      case 'confirmada': return 'Confirmada';
      case 'pendiente_confirmacion_paciente': return 'Pendiente (Paciente)';
      case 'pendiente_confirmacion_especialista': return 'Pendiente (Especialista)';
      case 'cancelada_paciente': return 'Cancelada por Paciente';
      case 'cancelada_especialista': return 'Cancelada por Especialista';
      case 'reagendada': return 'Reagendada';
      default: return estado;
    }
  }
}