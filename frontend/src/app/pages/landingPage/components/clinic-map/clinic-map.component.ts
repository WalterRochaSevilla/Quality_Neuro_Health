// clinic-map.component.ts
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Inject, PLATFORM_ID } from '@angular/core';
import { MapIconService } from '../../services/map-icon.service';
import { ClinicUtilsService } from '../../services/clinic-utils.service';

@Component({
  selector: 'app-clinic-map',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './clinic-map.component.html',
  styleUrls: ['./clinic-map.component.css']
})
export class ClinicMapComponent implements OnInit, OnDestroy {
  @Input() userLocation: { lat: number; lng: number } | null = null;
  @Input() staticClinics: any[] = [];

  private map: any = null;
  private markerCluster: any = null;
  private leafletLoaded = false;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private mapIconService: MapIconService,
    private clinicUtilsService: ClinicUtilsService
  ) {}

  async ngOnInit(): Promise<void> {
    if (isPlatformBrowser(this.platformId)) {
      await this.loadLeaflet();
      if (this.userLocation) {
        this.initMap();
      }
    }
  }

  private async loadLeaflet(): Promise<void> {
    if (this.leafletLoaded) return;

    try {
      const leaflet = await import('leaflet');
      await import('leaflet.markercluster');

      this.configureLeafletIcons(leaflet);
      this.leafletLoaded = true;
    } catch (err) {
      console.error('Error loading Leaflet:', err);
    }
  }

  private configureLeafletIcons(leaflet: any): void {
    delete (leaflet.Icon.Default.prototype as any)._getIconUrl;
    leaflet.Icon.Default.mergeOptions({
      iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
      iconUrl: 'assets/leaflet/marker-icon.png',
      shadowUrl: 'assets/leaflet/marker-shadow.png'
    });
  }

  private initMap(): void {
    if (!this.leafletLoaded || !window['L'] || !this.userLocation) {
      console.error('Leaflet no está disponible o no hay ubicación');
      return;
    }

    const L = window['L'];

    try {
      this.initializeMap(L);
      this.addUserLocationMarker(L);
      this.addClinicsToMap(L);
    } catch (error) {
      console.error('Error inicializando mapa:', error);
    }
  }

  private initializeMap(L: any): void {
    this.map = L.map('map').setView([this.userLocation!.lat, this.userLocation!.lng], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map);

    this.markerCluster = L.markerClusterGroup({
      spiderfyOnMaxZoom: true,
      showCoverageOnHover: false,
      zoomToBoundsOnClick: true
    });
  }

  private addUserLocationMarker(L: any): void {
    const userIcon = L.divIcon({
      className: 'user-marker',
      html: '<div class="pulse-dot"></div>',
      iconSize: [20, 20]
    });

    L.marker([this.userLocation!.lat, this.userLocation!.lng], { icon: userIcon })
      .bindPopup('Tu ubicación')
      .addTo(this.map);
  }

  private addClinicsToMap(L: any): void {
    const clinics = this.staticClinics.map(clinic => this.transformClinicData(clinic));
    this.processClinicsData(clinics, L);
  }

  private transformClinicData(clinic: any): any {
    return {
      ...clinic,
      lat: clinic.lat,
      lon: clinic.lng,
      tags: {
        name: clinic.name,
        healthcare: clinic.type,
        "contact:phone": clinic.phone,
        opening_hours: clinic.hours
      }
    };
  }

  private processClinicsData(clinics: any[], L: any): void {
    if (!this.map || !this.markerCluster) return;

    this.markerCluster.clearLayers();

    clinics.forEach(clinic => {
      this.addClinicMarker(clinic, L);
    });

    this.map.addLayer(this.markerCluster);
    this.fitMapToBounds();
    this.addMapLegend(L);
  }

  private addClinicMarker(clinic: any, L: any): void {
    const coordinates = this.clinicUtilsService.extractClinicCoordinates(clinic);
    if (!coordinates) {
      console.warn('Clínica sin coordenadas válidas:', clinic);
      return;
    }

    const clinicType = this.clinicUtilsService.determineClinicType(clinic);
    const iconConfig = this.mapIconService.getIconConfig(clinicType);
    const customIcon = this.createClinicIcon(iconConfig, L);
    
    const marker = L.marker([coordinates.lat, coordinates.lon], { icon: customIcon });
    const popupContent = this.clinicUtilsService.createPopupContent(
      clinic, 
      iconConfig.name
    );
    
    marker.bindPopup(popupContent);
    this.markerCluster.addLayer(marker);
  }

  private createClinicIcon(iconConfig: any, L: any): any {
    return L.divIcon({
      className: 'clinic-marker',
      html: `
        <div class="clinic-icon" style="background-color: ${iconConfig.color}">
          ${iconConfig.emoji}
        </div>
      `,
      iconSize: [32, 32],
      iconAnchor: [16, 16],
      popupAnchor: [0, -16]
    });
  }

  private fitMapToBounds(): void {
    if (this.markerCluster.getLayers().length > 0) {
      const bounds = this.markerCluster.getBounds();
      if (bounds.isValid()) {
        this.map.fitBounds(bounds, { padding: [50, 50] });
      }
    }
  }

  private addMapLegend(L: any): void {
    const legend = L.control({ position: 'bottomright' });

    legend.onAdd = () => {
      const div = L.DomUtil.create('div', 'map-legend');
      const iconConfigs = this.mapIconService.getAllIconConfigs();

      let content = '<h4>Leyenda</h4>';
      content += iconConfigs.map(({ type, config }) => `
        <div class="legend-item">
          <div class="legend-icon" style="background-color: ${config.color}">
            ${config.emoji}
          </div>
          <span>${config.name}</span>
        </div>
      `).join('');

      div.innerHTML = content;
      return div;
    };

    legend.addTo(this.map);
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }
}