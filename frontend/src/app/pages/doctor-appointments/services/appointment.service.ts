import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

export interface Appointment {
  id: string;
  fecha: string;
  hora: string;
  pacienteId?: string;
  pacienteNombre?: string;
  especialistaId: string;
  estado: 'confirmada' | 'pendiente_confirmacion_paciente' | 'pendiente_confirmacion_especialista' | 'cancelada_paciente' | 'cancelada_especialista' | 'reagendada';
}

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  
  getAppointmentsBySpecialist(specialistId: string): Observable<Appointment[]> {
    // Mock data - en producción esto vendría de tu backend
    return of([
      {
        id: '1',
        fecha: new Date().toISOString().split('T')[0],
        hora: '10:00',
        pacienteNombre: 'Juan Pérez',
        especialistaId: specialistId,
        estado: 'confirmada'
      },
      {
        id: '2',
        fecha: new Date().toISOString().split('T')[0],
        hora: '11:00',
        pacienteNombre: 'María García',
        especialistaId: specialistId,
        estado: 'pendiente_confirmacion_paciente'
      }
    ]);
  }

  cancelAppointment(appointmentId: string): Observable<boolean> {
    // Lógica para cancelar cita
    console.log('Cancelling appointment:', appointmentId);
    return of(true);
  }

  rescheduleAppointment(appointmentId: string, newDate: string, newTime: string): Observable<boolean> {
    // Lógica para reagendar cita
    console.log('Rescheduling appointment:', appointmentId, 'to', newDate, newTime);
    return of(true);
  }
}