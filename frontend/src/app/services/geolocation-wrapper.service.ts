import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GeolocationWrapperService {
  
  async getCurrentPosition(): Promise<{lat: number; lng: number}> {
    const geo = (navigator as any)['geolocation'];
    
    if (!geo || typeof geo.getCurrentPosition !== 'function') {
      return this.getDefaultLocation();
    }

    return new Promise((resolve) => {
      geo.getCurrentPosition(
        (position: any) => resolve({
          lat: position.coords.latitude,
          lng: position.coords.longitude
        }),
        (error: any) => {
          console.warn('Geolocation error:', this.getGeolocationErrorText(error.code));
          resolve(this.getDefaultLocation());
        },
        { timeout: 5000, enableHighAccuracy: false }
      );
    });
  }

  async getLocationByIP(): Promise<{lat: number; lng: number}> {
    try {
      const response = await fetch('https://ipapi.co/json/');
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      
      if (!data.latitude || !data.longitude) {
        throw new Error('Invalid location data from IP service');
      }
      
      return { lat: data.latitude, lng: data.longitude };
    } catch (error) {
      if (error instanceof Error) {
        console.warn('IP geolocation failed:', error.message);
      } else {
        console.warn('IP geolocation failed: Unknown error');
      }
      return this.getDefaultLocation();
    }
  }

  private getGeolocationErrorText(errorCode: number): string {
    const errors: { [key: number]: string } = {
      1: 'Permission denied',
      2: 'Position unavailable',
      3: 'Timeout'
    };
    return errors[errorCode] || `Error code: ${errorCode}`;
  }

  getDefaultLocation(): {lat: number; lng: number} {
    return { lat: -17.371486977105853, lng: -66.1439330529856 };
  }
}