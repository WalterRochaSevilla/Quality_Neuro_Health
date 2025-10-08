import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { AppointmentItemComponent } from '../appointment-item/appointment-item.component';

export interface Appointment {
  id: string;
  fecha: string;
  hora: string;
  pacienteId?: string;
  pacienteNombre?: string;
  especialistaId: string;
  estado: 'confirmada' | 'pendiente_confirmacion_paciente' | 'pendiente_confirmacion_especialista' | 'cancelada_paciente' | 'cancelada_especialista' | 'reagendada';
}

@Component({
  selector: 'app-appointment-list',
  standalone: true,
  imports: [CommonModule, DatePipe, AppointmentItemComponent],
  templateUrl: './appointment-list.component.html',
  styleUrls: ['./appointment-list.component.css']
})
export class AppointmentListComponent {
  @Input() appointments: Appointment[] = [];
  @Input() selectedDate: Date | null = null;
  @Input() isLoading = false;
  
  @Output() cancelAppointment = new EventEmitter<string>();
  @Output() rescheduleAppointment = new EventEmitter<Appointment>();

  // Enfoque alternativo con tipos explícitos
  handleCancelEvent(event: any): void {
    // Asegurarnos de que event es un string (el ID)
    if (typeof event === 'string') {
      this.cancelAppointment.emit(event);
    } else {
      console.error('Expected string for cancel event, got:', event);
    }
  }

  handleRescheduleEvent(event: any): void {
    // Verificar que event tiene la estructura de Appointment
    if (event && typeof event === 'object' && event.id && event.fecha) {
      this.rescheduleAppointment.emit(event as Appointment);
    } else {
      console.error('Expected Appointment for reschedule event, got:', event);
    }
  }
}