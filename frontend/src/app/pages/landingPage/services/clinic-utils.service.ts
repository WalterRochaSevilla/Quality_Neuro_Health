// services/clinic-utils.service.ts
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ClinicUtilsService {
  
  determineClinicType(clinic: any): string {
    const tags = clinic.tags || {};
    const name = (clinic.name || tags.name || '').toLowerCase();

    const typeMappings: { [key: string]: string[] } = {
      psychiatrist: ['psychiatrist', 'psiquiatr'],
      psychotherapist: ['psychotherapist', 'psicolog'],
      mental_health: ['mental_health', 'salud mental'],
      hospital: ['hospital', 'hospital']
    };

    for (const [type, keywords] of Object.entries(typeMappings)) {
      if (keywords.some(keyword => 
        tags.healthcare === keyword || name.includes(keyword)
      )) {
        return type;
      }
    }

    return 'general';
  }

  extractClinicCoordinates(clinic: any): { lat: number; lon: number } | null {
    const lat = clinic.lat || clinic.latitude || clinic.location?.lat;
    const lon = clinic.lon || clinic.lng || clinic.location?.lng || clinic.location?.lon;
    
    return (lat && lon) ? { lat, lon } : null;
  }

  createPopupContent(clinic: any, clinicTypeName: string): string {
    const name = clinic.name || clinic.tags?.name;
    const phone = clinic.phone || clinic.tags?.['contact:phone'];
    const hours = clinic.hours || clinic.tags?.opening_hours;

    const parts = [
      `<b>${name || 'Centro de Salud Mental'}</b>`,
      `<br><em>Tipo: ${clinicTypeName}</em>`,
      phone ? `<br>Teléfono: ${phone}` : '',
      hours ? `<br>Horario: ${hours}` : ''
    ];

    return parts.filter(part => part).join('');
  }
}