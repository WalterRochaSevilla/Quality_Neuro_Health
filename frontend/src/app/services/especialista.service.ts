import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface Especialista {
  id: string;
  name: string;
  speciality: string;
  hours: string[];
  occupiedHours: string[];
  patients: string[];
}

@Injectable({
  providedIn: 'root'
})
export class EspecialistaService {
  private readonly apiUrl = 'http://localhost:8080/api/especialistas';

  constructor(private readonly http: HttpClient) {}

  getAllEspecialistas(): Observable<Especialista[]> {
    return this.http.get<Especialista[]>(this.apiUrl);
  }

  getHorariosByEspecialistaId(id: string, fecha: string): Observable<{hours: string[], occupiedHours: string[]}> {
    return this.http.get<{hours: string[], occupiedHours: string[]}>(`${this.apiUrl}/${id}/horarios?fecha=${fecha}`);
  }
  
  ocuparHora(id: string, hour: string, fecha: string, userId: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/ocupar-hora`, { hour, fecha, userId });
  }

  getPatientsByEspecialistaId(id: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}/patients`);
  }
    
}