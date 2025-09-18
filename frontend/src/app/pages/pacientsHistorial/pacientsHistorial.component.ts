import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
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
    { value: 0, name: 'Enero' },
    { value: 1, name: 'Febrero' },
    { value: 2, name: 'Marzo' },
    { value: 3, name: 'Abril' },
    { value: 4, name: 'Mayo' },
    { value: 5, name: 'Junio' },
    { value: 6, name: 'Julio' },
    { value: 7, name: 'Agosto' },
    { value: 8, name: 'Septiembre' },
    { value: 9, name: 'Octubre' },
    { value: 10, name: 'Noviembre' },
    { value: 11, name: 'Diciembre' }
  ];

  constructor(
    private authService: AuthService,
    private especialistaService: EspecialistaService,
    private emocionService: EmocionService,
    private examenService: ExamenService,
    private router: Router

  ) {}

  ngOnInit(): void {
    const especialista = this.authService.getUsuario();
    if (!especialista || !especialista.id) return;

    this.especialistaService.getPatientsByEspecialistaId(especialista.id).subscribe(pacientes => {
      const cargarPacientes = pacientes.map((pac: any) =>
        Promise.all([
          this.emocionService.obtenerDiario(pac.id).toPromise(),
          this.examenService.obtenerExamenes(pac.id).toPromise()
        ]).then(([emociones, examenes]) => {
          const entries: (EmotionEntry | ExamEntry)[] = [];

          if (emociones?.entries) {
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

          if (examenes?.entries) {
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

          return {
            id: pac.id,
            name: pac.nombre,
            entries: entries.sort((a, b) => b.date.getTime() - a.date.getTime()) // opcional: ordenar por fecha descendente
          };
        })
      );

      Promise.all(cargarPacientes).then(pacientesCompletos => {
        this.patients = pacientesCompletos;
      });
    });
  }

  onPatientSelect(patientId: string) {
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

  filterByType(event: Event) {
    const type = (event.target as HTMLSelectElement).value;
    this.applyFilters(type, null);
  }

  filterByMonth(event: Event) {
    const month = (event.target as HTMLSelectElement).value;
    this.applyFilters(null, month);
  }

  private applyFilters(typeFilter: string | null, monthFilter: string | null) {
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

  clearFilters() {
    this.filteredEntries = this.selectedPatient ? [...this.selectedPatient.entries] : [];
    const selects = document.querySelectorAll<HTMLSelectElement>('.filter-select');
    selects.forEach(select => select.value = 'all');
  }
  goToHome() {
    this.router.navigate(['/']);
  }
}
