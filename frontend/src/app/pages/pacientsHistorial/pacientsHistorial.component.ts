import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { AuthService } from '../../auth.service';
import { EspecialistaService } from '../../services/especialista.service';
import { EmocionService } from '../../services/emocion.service';
import { ExamenService } from '../../services/examen.service';

interface HistorialEntry {
  type: 'emotion' | 'exam';
  date: Date;
}

interface EmotionEntry extends HistorialEntry {
  emotion: string;
  notes: string;
  tags?: string[];
}

interface ExamEntry extends HistorialEntry {
  examName: string;
  description: string;
  result: string;
}

interface Patient {
  id: string;
  name: string;
  entries: (EmotionEntry | ExamEntry)[];
}

interface PacienteData {
  id: string;
  nombre: string;
}

@Component({
  selector: 'app-patients-historial',
  templateUrl: './pacientsHistorial.component.html',
  styleUrls: ['./pacientsHistorial.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class PatientsHistorialComponent implements OnInit {
  patients: Patient[] = [];
  selectedPatientId: string = '';
  filteredEntries: (EmotionEntry | ExamEntry)[] = [];
  months = [
    { value: 0, name: 'Enero' }, { value: 1, name: 'Febrero' },
    { value: 2, name: 'Marzo' }, { value: 3, name: 'Abril' },
    { value: 4, name: 'Mayo' }, { value: 5, name: 'Junio' },
    { value: 6, name: 'Julio' }, { value: 7, name: 'Agosto' },
    { value: 8, name: 'Septiembre' }, { value: 9, name: 'Octubre' },
    { value: 10, name: 'Noviembre' }, { value: 11, name: 'Diciembre' }
  ];

  constructor(
    private readonly authService: AuthService,
    private readonly especialistaService: EspecialistaService,
    private readonly emocionService: EmocionService,
    private readonly examenService: ExamenService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.loadPatientsData();
  }

  private loadPatientsData(): void {
    const especialista = this.authService.getUsuario();
    if (!especialista?.id) return;

    this.especialistaService.getPatientsByEspecialistaId(especialista.id)
      .subscribe(pacientes => {
        const pacientesPromises = pacientes.map((pac: PacienteData) => 
          this.loadPatientWithEntries(pac)
        );
        Promise.all(pacientesPromises).then(pacientesCompletos => {
          this.patients = pacientesCompletos;
        });
      });
  }

  private async loadPatientWithEntries(pac: PacienteData): Promise<Patient> {
    const [emociones, examenes] = await Promise.all([
      lastValueFrom(this.emocionService.obtenerDiario(pac.id)),
      lastValueFrom(this.examenService.obtenerExamenes(pac.id))
    ]);

    const entries = this.processEntries(emociones, examenes);
    return this.createPatient(pac, entries);
  }

  private processEntries(emociones: any, examenes: any): (EmotionEntry | ExamEntry)[] {
    const entries: (EmotionEntry | ExamEntry)[] = [];
    
    this.addEmotionEntries(emociones, entries);
    this.addExamEntries(examenes, entries);
    
    return entries.sort((a, b) => b.date.getTime() - a.date.getTime());
  }

  private addEmotionEntries(emociones: any, entries: (EmotionEntry | ExamEntry)[]): void {
    if (!emociones?.entries) return;

    emociones.entries.forEach((e: any) => {
      entries.push({
        type: 'emotion',
        date: new Date(e.date),
        emotion: e.emotion,
        notes: e.notes,
        tags: e.tags || []
      });
    });
  }

  private addExamEntries(examenes: any, entries: (EmotionEntry | ExamEntry)[]): void {
    if (!examenes?.entries) return;

    examenes.entries.forEach((e: any) => {
      entries.push({
        type: 'exam',
        date: new Date(e.date),
        examName: e.name,
        description: e.description,
        result: e.result
      });
    });
  }

  private createPatient(pac: PacienteData, entries: (EmotionEntry | ExamEntry)[]): Patient {
    return {
      id: pac.id,
      name: pac.nombre,
      entries: entries
    };
  }

  onPatientSelect(patientId: string): void {
    this.selectedPatientId = patientId;
    this.applyFilters('all', 'all');
  }

  get selectedPatient(): Patient | undefined {
    return this.patients.find(p => p.id === this.selectedPatientId);
  }

  isEmotion(entry: HistorialEntry): entry is EmotionEntry {
    return entry.type === 'emotion';
  }

  isExam(entry: HistorialEntry): entry is ExamEntry {
    return entry.type === 'exam';
  }

  filterByType(event: Event): void {
    const type = (event.target as HTMLSelectElement).value;
    this.applyFilters(type, null);
  }

  filterByMonth(event: Event): void {
    const month = (event.target as HTMLSelectElement).value;
    this.applyFilters(null, month);
  }

  private applyFilters(typeFilter: string | null, monthFilter: string | null): void {
    if (!this.selectedPatient) {
      this.filteredEntries = [];
      return;
    }

    this.filteredEntries = this.selectedPatient.entries.filter(entry => {
      const typeMatch = !typeFilter || typeFilter === 'all' || entry.type === typeFilter;
      const monthMatch = !monthFilter || monthFilter === 'all' ||
        entry.date.getMonth() === parseInt(monthFilter);
      return typeMatch && monthMatch;
    });
  }

  clearFilters(): void {
    this.filteredEntries = this.selectedPatient ? [...this.selectedPatient.entries] : [];
    const selects = document.querySelectorAll<HTMLSelectElement>('.filter-select');
    selects.forEach(select => select.value = 'all');
  }

  goToHome(): void {
    this.router.navigate(['/']);
  }
}