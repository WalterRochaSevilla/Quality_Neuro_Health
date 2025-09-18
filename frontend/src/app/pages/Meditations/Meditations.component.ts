import { Component, OnInit, OnDestroy } from '@angular/core';
import { MeditationsService, Meditation } from './meditations.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MeditationPlayerComponent } from './meditation-player/meditation-player.component';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Subscription } from 'rxjs';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-meditations',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MeditationPlayerComponent,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './meditations.component.html',
  styleUrls: ['./meditations.component.css']
})
export class MeditationsComponent implements OnInit, OnDestroy {
  meditations: Meditation[] = [];
  selectedMeditation: Meditation | null = null;
  playerVisible = false;

  isadmin: boolean = false;
  isLoggedIn: boolean = false;
  showAdminForm = false;
  isEditing = false;
  isLoading: boolean = false;
  currentMeditationForForm: Partial<Meditation> = this.getEmptyMeditationForm();

  private authSubscription: Subscription | undefined;
  private meditationsSubscription: Subscription | undefined;

  meditationTypes: Meditation['type'][] = ['audio', 'video', 'youtube'];

  constructor(
    private meditationsService: MeditationsService,
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    const usuario = this.authService.getUsuario();
    this.isLoggedIn = !!usuario;
    this.isadmin = usuario?.rol === 'admin';
    this.loadMeditations();
  }

  loadMeditations(): void {
    this.isLoading = true;
    this.meditationsSubscription = this.meditationsService.getMeditations().subscribe({
      next: data => {
        this.meditations = data;
        this.isLoading = false;
      },
      error: err => {
        console.error('Error al cargar meditaciones:', err);
        this.isLoading = false;
      }
    });
  }

  ngOnDestroy(): void {
    this.authSubscription?.unsubscribe();
    this.meditationsSubscription?.unsubscribe();
  }

  getEmptyMeditationForm(): Partial<Meditation> {
    return {
      title: '',
      description: '',
      duration: '',
      type: 'audio',
      url: ''
    };
  }

  playMeditation(meditation: Meditation): void {
    this.selectedMeditation = meditation;
    this.playerVisible = true;
  }

  closePlayer(): void {
    this.playerVisible = false;
    this.selectedMeditation = null;
  }

  goToHome() {
    this.router.navigate(['/']);
  }

  openAddMeditationForm(): void {
    this.isEditing = false;
    this.currentMeditationForForm = this.getEmptyMeditationForm();
    this.showAdminForm = true;
  }

  openEditMeditationForm(meditation: Meditation): void {
    this.isEditing = true;
    this.currentMeditationForForm = { ...meditation };
    this.showAdminForm = true;
  }

  closeAdminForm(): void {
    this.showAdminForm = false;
    this.currentMeditationForForm = this.getEmptyMeditationForm();
  }

  saveMeditation(): void {
    if (!this.currentMeditationForForm.title || !this.currentMeditationForForm.url || !this.currentMeditationForForm.type) {
        alert('Por favor, completa los campos obligatorios: Título, URL y Tipo.');
        return;
    }

    const meditationData = this.currentMeditationForForm as Omit<Meditation, 'id'>;
    const fullMeditationData = this.currentMeditationForForm as Meditation;
    this.isLoading = true;

    if (this.isEditing && fullMeditationData.id) {
      this.meditationsService.updateMeditation(fullMeditationData).subscribe({
        next: () => {
          this.closeAdminForm();
          this.loadMeditations();
        },
        error: (err) => {
          console.error('Error al actualizar meditación:', err);
          this.isLoading = false;
        }
      });
    } else {
      this.meditationsService.addMeditation(meditationData).subscribe({
        next: () => {
          this.closeAdminForm();
          this.loadMeditations();
        },
        error: (err) => {
          console.error('Error al agregar meditación:', err);
          this.isLoading = false;
        }
      });
    }
  }

  deleteMeditation(id: number | undefined): void {
    if (id === undefined) {
        console.error('ID de meditación no definido para eliminar');
        return;
    }
    if (confirm('¿Estás seguro de que quieres eliminar esta meditación?')) {
      this.isLoading = true;
      this.meditationsService.deleteMeditation(id).subscribe({
        next: () => {
          if (this.selectedMeditation?.id === id) {
            this.closePlayer();
          }
          this.loadMeditations();
        },
        error: (err) => {
          console.error('Error al eliminar meditación:', err);
          this.isLoading = false;
        }
      });
    }
  }
}