import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  private readonly apiUrl = 'http://localhost:8080/foro'

  constructor(private readonly http: HttpClient) {}

  getComments(): Observable<any[]> {

    const apiUrlComments = `${this.apiUrl}/publicaciones`;

    return this.http.get<any[]>(apiUrlComments);
  }

  postComment(usuarioId: string, titulo: string, contenido: string, tema: string): Observable<any> {
      const body = {
        usuarioId,
        titulo,
        contenido,
        tema
      };
    
      const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
      const apiUrlComments = `${this.apiUrl}/publicaciones`;
    
      return this.http.post(apiUrlComments, body, { headers });
    }  
}
