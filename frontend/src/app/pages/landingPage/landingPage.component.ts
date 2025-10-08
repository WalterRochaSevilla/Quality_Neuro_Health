import { Component, OnInit, AfterViewInit, OnDestroy, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule, DatePipe, isPlatformBrowser } from '@angular/common';
import { Router, RouterModule } from '@angular/router'; // Agregar Router aquí

import { AuthService } from '../../auth.service';
import { EspecialistaService } from '../../services/especialista.service';
import { GeolocationWrapperService } from '../../services/geolocation-wrapper.service';
import { NavigationService } from './services/navigation.service';
// Importar el componente existente
import { CalendarComponent } from '../doctor-appointments/components/calendar/calendar.component';

@Component({
  selector: 'app-landingPage',
  standalone: true,
  imports: [CommonModule, RouterModule, CalendarComponent],
  templateUrl: './landingPage.component.html',
  styleUrls: ['./landingPage.component.css'],
  providers: [DatePipe]
})
export class LandingPageComponent implements OnInit, AfterViewInit, OnDestroy {
  selectedDate: Date | null = null;
  currentDate = new Date();
  isLoggedIn = false;
  isEspecialista = false;
  userLocation: { lat: number; lng: number } | null = null;
  staticClinics: any[] = [];
  
  // Para el calendario existente
  appointmentsMap: Map<string, any[]> = new Map();

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private especialistaService: EspecialistaService,
    private geolocationService: GeolocationWrapperService,
    private navigationService: NavigationService,
    private datePipe: DatePipe,
    private router: Router, // Agregar Router aquí
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.loadStaticClinics();
  }

  ngOnInit(): void {
    this.checkAuthStatus();
    this.navigationService.setupFragmentNavigation();
  }

  async ngAfterViewInit(): Promise<void> {
    if (isPlatformBrowser(this.platformId) && !this.isEspecialista) {
      await this.initializeUserLocation();
    }
  }

  private loadStaticClinics(): void {
    this.http.get<any[]>('./data/mental-health-centers.json').subscribe(data => {
      this.staticClinics = data;
    });
  }

  private async initializeUserLocation(): Promise<void> {
    this.userLocation = await this.geolocationService.getLocationByIP();
  }

  private checkAuthStatus(): void {
    const usuario = this.authService.getUsuario();
    this.isLoggedIn = !!usuario;
    this.isEspecialista = usuario?.rol === 'especialista';
  }

  // Métodos para el calendario existente
  onDateSelected(date: Date): void {
    this.selectedDate = date;
    console.log('Fecha seleccionada:', date);
  }

  onPrevMonth(): void {
    this.currentDate = new Date(
      this.currentDate.getFullYear(),
      this.currentDate.getMonth() - 1
    );
    this.selectedDate = null;
  }

  onNextMonth(): void {
    this.currentDate = new Date(
      this.currentDate.getFullYear(),
      this.currentDate.getMonth() + 1
    );
    this.selectedDate = null;
  }

  confirmSelection(): void {
    if (this.selectedDate) {
      this.navigationService.navigateWithDate(this.selectedDate, this.datePipe);
    }
  }

  logout(): void {
    if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
      this.authService.logout();
      this.isLoggedIn = false;
      this.isEspecialista = false;
      this.router.navigate(['/']).then(() => {
        window.location.reload();
      });
    }
  }

  ngOnDestroy(): void {
    this.navigationService.cleanup();
  }
}