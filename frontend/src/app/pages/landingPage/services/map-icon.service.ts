import { Injectable } from '@angular/core';

export interface IconConfig {
  emoji: string;
  color: string;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class MapIconService {
  private readonly iconConfigs: { [key: string]: IconConfig } = {
    psychiatrist: { emoji: '🧠', color: '#FF6B6B', name: 'Psiquiatría' },
    psychotherapist: { emoji: '💬', color: '#4ECDC4', name: 'Psicoterapia' },
    mental_health: { emoji: '❤️', color: '#FFD166', name: 'Salud Mental' },
    hospital: { emoji: '🏥', color: '#6A0572', name: 'Hospital' },
    general: { emoji: '🏥', color: '#5A92C2', name: 'Centro de Salud' }
  };

  getIconConfig(clinicType: string): IconConfig {
    return this.iconConfigs[clinicType] || this.iconConfigs['general'];
  }

  getAllIconConfigs(): { type: string; config: IconConfig }[] {
    return Object.entries(this.iconConfigs).map(([type, config]) => ({
      type,
      config
    }));
  }
}