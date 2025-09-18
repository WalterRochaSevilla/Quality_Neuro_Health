import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/usuarios';
  private usuario: any = null;
  private isAdminSubject = new BehaviorSubject<boolean>(false);
  isAdmin$ = this.isAdminSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    // Mock para administrador
    if (email === 'admin@neurohealth.com' && password === 'admin123') {
      const mockAdmin = {
        id: 'admin1',
        nombre: 'Admin',
        apellido: 'Principal',
        email: 'admin@neurohealth.com',
        rol: 'administrador',
        activo: true
      };
      return of(mockAdmin).pipe(delay(500));
    }
    
    // Lógica normal para otros usuarios
    const params = new URLSearchParams();
    params.append('email', email);
    params.append('contrasena', password);
  
    const headers = new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' });
    const apiLoginUrl = `${this.apiUrl}/login`;
  
    return this.http.post(apiLoginUrl, params.toString(), { headers });
  }

  signup(nombre: string, apellido: string, email: string, password: string): Observable<any> {
    const body = {
      nombre,
      apellido,
      email,
      contrasena: password,
      fechaRegistro: new Date(),
      rol: 'usuario'
    };
  
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const apiSignupUrl = `${this.apiUrl}/registro`;
  
    return this.http.post(apiSignupUrl, body, { headers });
  }  

  guardarUsuario(usuario: any) {
    localStorage.setItem('usuario', JSON.stringify(usuario));
    this.usuario = usuario;
    this.isAdminSubject.next(usuario.rol === 'administrador');
  }

  getUsuario() {
    if (!this.usuario) {
      const usuarioGuardado = localStorage.getItem('usuario');
      this.usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null;
      if (this.usuario) {
        this.isAdminSubject.next(this.usuario.rol === 'administrador');
      }
    }
    return this.usuario;
  }

  logout() {
    localStorage.removeItem('usuario');
    this.usuario = null;
    this.isAdminSubject.next(false);
  }

  isAdmin(): boolean {
    return this.usuario?.rol === 'admin';
  }
}