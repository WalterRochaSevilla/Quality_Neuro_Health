import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Cita {
  id?: string;
  usuarioId: string;
  especialistaId: string;
  usuario: string;
  fecha: string;
  hora: string;
}

@Injectable({
  providedIn: 'root'
})
export class CitaService {
  private apiUrl = 'http://localhost:8080/citas'; // Asegúrate de usar la URL correcta

  constructor(private http: HttpClient) {}

  // Crear una nueva cita
  crearCita(cita: Cita): Observable<Cita> {
    return this.http.post<Cita>(this.apiUrl, cita);
  }

  // Obtener todas las citas
  obtenerCitas(): Observable<Cita[]> {
    return this.http.get<Cita[]>(this.apiUrl);
  }

  // Obtener citas por usuario
  obtenerCitasPorUsuario(usuarioId: string): Observable<Cita[]> {
    return this.http.get<Cita[]>(`${this.apiUrl}/usuario/${usuarioId}`);
  }

  // Obtener citas por especialista
  obtenerCitasPorEspecialista(especialistaId: string): Observable<Cita[]> {
    return this.http.get<Cita[]>(`${this.apiUrl}/especialista/${especialistaId}`);
  }
}
