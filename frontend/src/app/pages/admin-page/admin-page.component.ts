import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.css'],
  providers: [AdminService, DatePipe]
})
export class AdminPageComponent implements OnInit {
  usuarios: any[] = [];
  citas: any[] = [];
  actividadLog: any[] = [];
  currentView: 'users' | 'appointments' | 'logs' = 'users';

  constructor(
    private readonly adminService: AdminService,
    private readonly authService: AuthService,
    private readonly datePipe: DatePipe,
    private readonly router: Router
  ) {}

  ngOnInit() {
    this.loadUsers();
    this.loadAppointments();
    this.loadActivityLog();
  }

  loadUsers() {
    this.adminService.getUsers().subscribe((users: Object) => {
      this.usuarios = users as any[];
    });
  }

  loadAppointments() {
    this.adminService.getAppointments().subscribe((appointments: Object) => {
      this.citas = appointments as any[];
    });
  }

  loadActivityLog() {
    this.adminService.getActivityLog().subscribe((logs: any[]) => {
      this.actividadLog = logs;
    });
  }

  toggleUserStatus(user: any) {
    const newStatus = !user.activo;
    this.adminService.updateUserStatus(user.id, newStatus).subscribe(() => {
      user.activo = newStatus;
      this.logAction(`Usuario ${user.email} ${newStatus ? 'activado' : 'bloqueado'}`);
    });
  }

  deleteUser(userId: string) {
    if (confirm('¿Está seguro de eliminar este usuario?')) {
      this.adminService.deleteUser(userId).subscribe(() => {
        this.usuarios = this.usuarios.filter(u => u.id !== userId);
        this.logAction(`Usuario eliminado: ID ${userId}`);
      });
    }
  }

  cancelAppointment(appointmentId: string) {
    if (confirm('¿Está seguro de cancelar esta cita?')) {
      this.adminService.cancelAppointment(appointmentId).subscribe(() => {
        this.citas = this.citas.filter(c => c.id !== appointmentId);
        this.logAction(`Cita cancelada: ID ${appointmentId}`);
      });
    }
  }

  private logAction(action: string) {
    const user = this.authService.getUsuario();
    this.actividadLog.unshift({
      timestamp: new Date(),
      action: action,
      admin: `${user.nombre} ${user.apellido}`
    });
  }

  formatDate(date: Date | string, format: string): string {
    return this.datePipe.transform(date, format) || '';
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}