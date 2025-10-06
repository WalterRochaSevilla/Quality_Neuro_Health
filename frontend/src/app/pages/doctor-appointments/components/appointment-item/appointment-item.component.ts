import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

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
  selector: 'app-appointment-item',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="da-appointment-item">
      <div class="da-appointment-time">{{ appointment.hora }}</div>
      <div class="da-appointment-patient">
        Paciente: {{ appointment.pacienteNombre || 'No especificado' }}
      </div>
      <div class="da-appointment-status" [ngClass]="'status-' + appointment.estado.toLowerCase()">
        Estado: {{ getEstadoText(appointment.estado) }}
      </div>
      
      <div class="da-appointment-actions" 
           *ngIf="appointment.estado !== 'cancelada_especialista' && appointment.estado !== 'cancelada_paciente'">
        <button class="da-action-button da-button-cancel" 
                (click)="onCancel()">
          Cancelar
        </button>
        <button class="da-action-button da-button-reschedule" 
                (click)="onReschedule()">
          Reagendar
        </button>
      </div>
      
      <div *ngIf="appointment.estado === 'cancelada_especialista' || appointment.estado === 'cancelada_paciente'" 
           class="da-appointment-cancelled-info">
        Cita cancelada.
      </div>
    </div>
  `,
  styleUrls: ['./appointment-item.component.css']
})
export class AppointmentItemComponent {
  @Input() appointment!: Appointment;
  
  @Output() Appointment_cancel = new EventEmitter<string>();
  @Output() reschedule = new EventEmitter<Appointment>();

  getEstadoText(estado: string): string {
    const estadoMap: { [key: string]: string } = {
      'confirmada': 'Confirmada',
      'pendiente_confirmacion_paciente': 'Pendiente (Paciente)',
      'pendiente_confirmacion_especialista': 'Pendiente (Especialista)',
      'cancelada_paciente': 'Cancelada por Paciente',
      'cancelada_especialista': 'Cancelada por Especialista',
      'reagendada': 'Reagendada'
    };
    return estadoMap[estado] || estado;
  }

  onCancel(): void {
    this.Appointment_cancel.emit(this.appointment.id);
  }

  onReschedule(): void {
    this.reschedule.emit(this.appointment);
  }
}