import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reschedule-prompt',
  standalone: true,
  imports: [CommonModule], 
  templateUrl: './reschedule-prompt.component.html',
  styleUrls: ['./reschedule-prompt.component.css']
})
export class ReschedulePromptComponent {
  @Input() message: string | null = null;
  @Output() rescheduleCancelled = new EventEmitter<void>();

  onCancelReschedule(): void {
    this.rescheduleCancelled.emit();
  }
}