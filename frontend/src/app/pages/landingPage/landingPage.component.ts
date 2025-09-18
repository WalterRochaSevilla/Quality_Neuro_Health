import { Component, OnInit, AfterViewInit, OnDestroy, ElementRef, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule, DatePipe, isPlatformBrowser } from '@angular/common';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../auth.service';
import { EspecialistaService } from '../../services/especialista.service';
import { Subscription } from 'rxjs';

declare let L: any;

@Component({
  selector: 'app-landingPage',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './landingPage.component.html',
  styleUrls: ['./landingPage.component.css'],
  providers: [DatePipe]
})
export class landingPage implements OnInit, AfterViewInit, OnDestroy {
  selectedDate: Date | null = null;
  currentDate = new Date();
  calendarDays: (number | null)[][] = [];
  isLoggedIn = false;
  isEspecialista = false;
  private map: any = null;
  private markerCluster: any = null;
  private fragmentSubscription: Subscription | undefined;
  private leafletLoaded = false;
  userLocation: { lat: number; lng: number } | null = null;

  // Solución 1: Declaración única y consistente de staticClinics
  staticClinics: any[] = [];

  constructor(
    private router: Router,
    private datePipe: DatePipe,
    private authService: AuthService,
    private especialistaService: EspecialistaService,
    private route: ActivatedRoute,
    private el: ElementRef,
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object,
  ) {
    this.http.get<any[]>('assets/landing-page/mental-health-centers.json').subscribe(data => {
      this.staticClinics = data;
      console.log('Centros estáticos cargados:', this.staticClinics);
    });
  }

  ngOnInit() {
    this.generateCalendar(this.currentDate);
    this.checkAuthStatus();
  }

  async ngAfterViewInit(): Promise<void> {
    this.fragmentSubscription = this.route.fragment.subscribe(fragment => {
      if (fragment) {
        setTimeout(() => this.scrollToFragment(fragment), 100);
      }
    });

    if (isPlatformBrowser(this.platformId)) {
      await this.loadLeaflet();
      if (!this.isEspecialista) {
        await this.getUserLocation();
        this.initMap();
      }
    }
  }

  private async loadLeaflet(): Promise<void> {
    if (this.leafletLoaded) return;

    try {
      const leaflet = await import('leaflet');
      await import('leaflet.markercluster');

      delete (leaflet.Icon.Default.prototype as any)._getIconUrl;
      leaflet.Icon.Default.mergeOptions({
        iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
        iconUrl: 'assets/leaflet/marker-icon.png',
        shadowUrl: 'assets/leaflet/marker-shadow.png'
      });

      this.leafletLoaded = true;
    } catch (err) {
      console.error('Error loading Leaflet:', err);
    }
  }

  async getUserLocation(): Promise<void> {
    return new Promise((resolve) => {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            this.userLocation = {
              lat: position.coords.latitude,
              lng: position.coords.longitude
            };
            resolve();
          },
          (error) => {
            console.error('Error obteniendo ubicación', error);
            this.userLocation = { lat: -17.371486977105853, lng: -66.1439330529856 };
            resolve();
          }
        );
      } else {
        console.error('Geolocation no soportada');
        this.userLocation = { lat: -17.371486977105853, lng: -66.1439330529856 };
        resolve();
      }
    });
  }

  scrollToFragment(fragment: string): void {
    try {
      const element = document.querySelector(`#${fragment}`);
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    } catch (e) {
      console.error('Error scrolling to fragment:', e);
    }
  }

  checkAuthStatus() {
    const usuario = this.authService.getUsuario();
    this.isLoggedIn = !!usuario;
    this.isEspecialista = usuario?.rol === 'especialista';
  }

  logout() {
    if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
      this.authService.logout();
      this.isLoggedIn = false;
      this.isEspecialista = false;
      this.router.navigate(['/']).then(() => {
        window.location.reload();
      });
    }
  }

  generateCalendar(date: Date) {
    const month = date.getMonth();
    const year = date.getFullYear();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const startDay = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1;

    this.calendarDays = [];
    let days: (number | null)[] = [];

    for (let i = 0; i < startDay; i++) {
      days.push(null);
    }

    for (let day = 1; day <= lastDay.getDate(); day++) {
      days.push(day);
      if (days.length === 7) {
        this.calendarDays.push(days);
        days = [];
      }
    }

    if (days.length > 0) {
      while (days.length < 7) {
        days.push(null);
      }
      this.calendarDays.push(days);
    }
  }

  selectDate(day: number | null) {
    if (day) {
      this.selectedDate = new Date(
        this.currentDate.getFullYear(),
        this.currentDate.getMonth(),
        day
      );
    }
  }

  isSelected(day: number | null): boolean {
    return !!day && !!this.selectedDate &&
      this.selectedDate.getDate() === day &&
      this.selectedDate.getMonth() === this.currentDate.getMonth() &&
      this.selectedDate.getFullYear() === this.currentDate.getFullYear();
  }

  prevMonth() {
    this.currentDate = new Date(
      this.currentDate.getFullYear(),
      this.currentDate.getMonth() - 1
    );
    this.generateCalendar(this.currentDate);
    this.selectedDate = null;
  }

  nextMonth() {
    this.currentDate = new Date(
      this.currentDate.getFullYear(),
      this.currentDate.getMonth() + 1
    );
    this.generateCalendar(this.currentDate);
    this.selectedDate = null;
  }

  confirmSelection(): void {
    if (this.selectedDate) {
      const formattedDate = this.datePipe.transform(this.selectedDate, 'yyyy-MM-dd');
      this.router.navigate(['/appointments'], {
        queryParams: { date: formattedDate }
      });
    }
  }

  ngOnDestroy(): void {
    if (this.fragmentSubscription) {
      this.fragmentSubscription.unsubscribe();
    }
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  initMap(): void {
    if (!this.leafletLoaded || !window['L'] || !this.userLocation) {
      console.error('Leaflet no está disponible o no hay ubicación');
      return;
    }

    const L = window['L'];

    try {
      delete (L.Icon.Default.prototype as any)._getIconUrl;
      L.Icon.Default.mergeOptions({
        iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
        iconUrl: 'assets/leaflet/marker-icon.png',
        shadowUrl: 'assets/leaflet/marker-shadow.png'
      });

      this.map = L.map('map').setView([this.userLocation.lat, this.userLocation.lng], 13);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      }).addTo(this.map);

      this.markerCluster = L.markerClusterGroup({
        spiderfyOnMaxZoom: true,
        showCoverageOnHover: false,
        zoomToBoundsOnClick: true
      });

      const userIcon = L.divIcon({
        className: 'user-marker',
        html: '<div class="pulse-dot"></div>',
        iconSize: [20, 20]
      });

      L.marker([this.userLocation.lat, this.userLocation.lng], { icon: userIcon })
        .bindPopup('Tu ubicación')
        .addTo(this.map);

      this.findMentalHealthClinics();
    } catch (error) {
      console.error('Error inicializando mapa:', error);
    }
  }

  // Solución 2: Método simplificado para obtener clínicas
  findMentalHealthClinics(): void {
    if (!this.userLocation) return;

    // Usar solo los datos estáticos
    const transformedClinics = this.staticClinics.map(clinic => ({
      ...clinic,
      lat: clinic.lat,
      lon: clinic.lng,
      tags: {
        name: clinic.name,
        healthcare: clinic.type,
        "contact:phone": clinic.phone,
        opening_hours: clinic.hours
      }
    }));

    this.processClinicsData(transformedClinics);
  }

  // Solución 3: Mover processClinicsData antes de su uso
  private processClinicsData(clinics: any[]): void {
    if (!this.map || !this.markerCluster || !window['L']) return;

    const L = window['L'];
    this.markerCluster.clearLayers();

    clinics.forEach(clinic => {
      const lat = clinic.lat || clinic.latitude || clinic.location?.lat;
      const lon = clinic.lon || clinic.lng || clinic.location?.lng || clinic.location?.lon;

      if (lat && lon) {
        const clinicType = this.determineClinicType(clinic);
        const customIcon = this.createClinicIcon(clinicType, L);
        const marker = L.marker([lat, lon], { icon: customIcon });

        const name = clinic.name || clinic.tags?.name;
        const phone = clinic.phone || clinic.tags?.['contact:phone'];
        const hours = clinic.hours || clinic.tags?.opening_hours;

        let popupContent = `<b>${name || 'Centro de Salud Mental'}</b>`;
        popupContent += `<br><em>Tipo: ${this.getClinicTypeName(clinicType)}</em>`;

        if (phone) {
          popupContent += `<br>Teléfono: ${phone}`;
        }

        if (hours) {
          popupContent += `<br>Horario: ${hours}`;
        }

        marker.bindPopup(popupContent);
        this.markerCluster.addLayer(marker);
      } else {
        console.warn('Clínica sin coordenadas válidas:', clinic);
      }
    });

    this.map.addLayer(this.markerCluster);

    if (clinics.length > 0) {
      const bounds = this.markerCluster.getBounds();
      if (bounds.isValid()) {
        this.map.fitBounds(bounds, { padding: [50, 50] });
      }
    }

    this.addMapLegend(L);
  }

  private determineClinicType(clinic: any): string {
    const tags = clinic.tags || {};
    const name = (clinic.name || tags.name || '').toLowerCase();

    if (tags.healthcare === 'psychiatrist' || name.includes('psiquiatr')) {
      return 'psychiatrist';
    }
    if (tags.healthcare === 'psychotherapist' || name.includes('psicolog')) {
      return 'psychotherapist';
    }
    if (tags.healthcare === 'mental_health' || name.includes('salud mental')) {
      return 'mental_health';
    }
    if (tags.healthcare === 'hospital' || name.includes('hospital')) {
      return 'hospital';
    }

    return 'general';
  }

  private createClinicIcon(clinicType: string, L: any): any {
    const icons = {
      psychiatrist: { emoji: '🧠', color: '#FF6B6B' },
      psychotherapist: { emoji: '💬', color: '#4ECDC4' },
      mental_health: { emoji: '❤️', color: '#FFD166' },
      hospital: { emoji: '🏥', color: '#6A0572' },
      general: { emoji: '🏥', color: '#5A92C2' }
    };

    const config = icons[clinicType as keyof typeof icons] || icons.general;

    return L.divIcon({
      className: 'clinic-marker',
      html: `
        <div class="clinic-icon" style="background-color: ${config.color}">
          ${config.emoji}
        </div>
      `,
      iconSize: [32, 32],
      iconAnchor: [16, 16],
      popupAnchor: [0, -16]
    });
  }

  private getClinicTypeName(clinicType: string): string {
    const names = {
      psychiatrist: 'Psiquiatría',
      psychotherapist: 'Psicoterapia',
      mental_health: 'Salud Mental',
      hospital: 'Hospital',
      general: 'Centro de Salud'
    };

    return names[clinicType as keyof typeof names] || 'Centro de Salud';
  }

  private addMapLegend(L: any): void {
    const legend = L.control({ position: 'bottomright' });

    legend.onAdd = () => {
      const div = L.DomUtil.create('div', 'map-legend');
      const types = [
        { type: 'psychiatrist', name: 'Psiquiatría' },
        { type: 'psychotherapist', name: 'Psicoterapia' },
        { type: 'mental_health', name: 'Salud Mental' },
        { type: 'hospital', name: 'Hospital' },
        { type: 'general', name: 'Otros' }
      ];

      let content = '<h4>Leyenda</h4>';
      types.forEach(t => {
        const config = this.getIconConfig(t.type);
        content += `
          <div class="legend-item">
            <div class="legend-icon" style="background-color: ${config.color}">
              ${config.emoji}
            </div>
            <span>${t.name}</span>
          </div>
        `;
      });

      div.innerHTML = content;
      return div;
    };

    legend.addTo(this.map);
  }

  private getIconConfig(clinicType: string): { emoji: string; color: string } {
    const icons = {
      psychiatrist: { emoji: '🧠', color: '#FF6B6B' },
      psychotherapist: { emoji: '💬', color: '#4ECDC4' },
      mental_health: { emoji: '❤️', color: '#FFD166' },
      hospital: { emoji: '🏥', color: '#6A0572' },
      general: { emoji: '🏥', color: '#5A92C2' }
    };

    return icons[clinicType as keyof typeof icons] || icons.general;
  }
}
