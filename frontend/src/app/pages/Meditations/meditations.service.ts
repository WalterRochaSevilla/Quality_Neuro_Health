import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of, throwError } from 'rxjs';
import { tap, catchError, map } from 'rxjs/operators';

export interface Meditation {
    id: number;
    url: string;
    type: 'audio' | 'video' | 'youtube';
    title?: string;
    description?: string;
    duration?: string;
}

@Injectable({
  providedIn: 'root'
})
export class MeditationsService {
  private meditationsUrl = 'meditations-assets/meditations.json'; // Path to your JSON
  private meditations: Meditation[] = [];
  private meditationsSubject = new BehaviorSubject<Meditation[]>([]);
  private dataLoaded = false;

  constructor(private http: HttpClient) {
    this.loadInitialMeditations();
  }

  private loadInitialMeditations(): void {
    this.http.get<Meditation[]>(this.meditationsUrl).pipe(
      tap(data => {
        this.meditations = data;
        this.meditationsSubject.next([...this.meditations]); // Emit a copy
        this.dataLoaded = true;
      }),
      catchError(err => {
        console.error('Error loading initial meditations:', err);
        this.dataLoaded = true; // Mark as loaded to prevent retry loops, even on error
        this.meditationsSubject.next([]); // Emit empty array on error
        return throwError(() => new Error('Failed to load meditations data'));
      })
    ).subscribe();
  }

  getMeditations(): Observable<Meditation[]> {
    // If data hasn't been loaded yet, the BehaviorSubject will provide its initial value (empty array)
    // or the value once loadInitialMeditations completes.
    return this.meditationsSubject.asObservable();
  }

  // Helper to get the next available ID
  private getNextId(): number {
    return this.meditations.length > 0 ? Math.max(...this.meditations.map(m => m.id)) + 1 : 1;
  }

  addMeditation(meditationData: Omit<Meditation, 'id'>): Observable<Meditation> {
    if (!this.dataLoaded) {
        return throwError(() => new Error('Meditations data not yet loaded. Cannot add.'));
    }
    const newMeditation: Meditation = {
      ...meditationData,
      id: this.getNextId()
    };
    this.meditations.push(newMeditation);
    this.meditationsSubject.next([...this.meditations]); // Emit updated list
    return of(newMeditation); // Simulate successful backend response
  }

  updateMeditation(meditationToUpdate: Meditation): Observable<Meditation> {
    if (!this.dataLoaded) {
        return throwError(() => new Error('Meditations data not yet loaded. Cannot update.'));
    }
    const index = this.meditations.findIndex(m => m.id === meditationToUpdate.id);
    if (index > -1) {
      this.meditations[index] = meditationToUpdate;
      this.meditationsSubject.next([...this.meditations]);
      return of(meditationToUpdate);
    }
    return throwError(() => new Error(`Meditation with id ${meditationToUpdate.id} not found`));
  }

  deleteMeditation(id: number): Observable<void> {
    if (!this.dataLoaded) {
        return throwError(() => new Error('Meditations data not yet loaded. Cannot delete.'));
    }
    const initialLength = this.meditations.length;
    this.meditations = this.meditations.filter(m => m.id !== id);
    if (this.meditations.length < initialLength) {
      this.meditationsSubject.next([...this.meditations]);
      return of(undefined); // Simulate successful backend response
    }
    return throwError(() => new Error(`Meditation with id ${id} not found for deletion`));
  }
}