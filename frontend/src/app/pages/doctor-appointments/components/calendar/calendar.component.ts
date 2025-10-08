import { Component, Input, Output, EventEmitter, OnChanges } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

export interface CalendarDay {
  date: Date;
  dayNumber: number;
  isCurrentMonth: boolean;
  hasAppointments: boolean;
  isToday: boolean;
}

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnChanges {
  @Input() currentDate!: Date;
  @Input() selectedDate: Date | null = null;
  @Input() appointmentsMap: Map<string, any[]> = new Map();

  @Output() dateSelected = new EventEmitter<Date>();
  @Output() prevMonth = new EventEmitter<void>();
  @Output() nextMonth = new EventEmitter<void>();

  calendarDays: (CalendarDay | null)[][] = [];

  ngOnChanges(): void {
    this.generateCalendar();
  }

  generateCalendar(): void {
    this.calendarDays = [];
    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();
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
        const dateKey = this.formatDate(dayDate);

        week.push({
          date: dayDate,
          dayNumber: dayDate.getDate(),
          isCurrentMonth: dayDate.getMonth() === month,
          hasAppointments: this.appointmentsMap.has(dateKey) && (this.appointmentsMap.get(dateKey)?.length || 0) > 0,
          isToday: this.isToday(dayDate)
        });
        currentCalDate.setDate(currentCalDate.getDate() + 1);
      }
      this.calendarDays.push(week);
    }
  }

  private formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  onDateSelect(date: Date): void {
    this.dateSelected.emit(date);
  }

  isSelected(date: Date | undefined | null): boolean {
    if (!date || !this.selectedDate) return false;
    return date.getFullYear() === this.selectedDate.getFullYear() &&
          date.getMonth() === this.selectedDate.getMonth() &&
          date.getDate() === this.selectedDate.getDate();
  }

  isToday(date: Date | undefined | null): boolean {
    if (!date) return false;
    const today = new Date();
    return date.getFullYear() === today.getFullYear() &&
          date.getMonth() === today.getMonth() &&
          date.getDate() === today.getDate();
  }

  onPrevMonth(): void {
    this.prevMonth.emit();
  }

  onNextMonth(): void {
    this.nextMonth.emit();
  }
}