import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'safeUrl' })
export class SafeUrlPipe implements PipeTransform {
  // Lista blanca de dominios permitidos
  private readonly allowedDomains = [
    'youtube.com',
    'www.youtube.com',
    'youtu.be',
    'www.youtu.be',
    'player.vimeo.com',
    'vimeo.com'
  ];

  transform(url: string, type?: string): string {
    if (type === 'youtube' || this.isYoutubeUrl(url)) {
      const videoId = this.extractYoutubeId(url);
      return `https://www.youtube.com/embed/${videoId}?autoplay=1&rel=0`;
    }
    if (this.isUrlSafe(url)) {
      return url;
    }
    return 'about:blank';
  }

  private isUrlSafe(url: string): boolean {
    try {
      if (!this.isAbsoluteUrl(url)) {
        return true;
      }
      const parsedUrl = new URL(url);
      const domain = parsedUrl.hostname;
      return this.allowedDomains.some(allowedDomain => 
        domain === allowedDomain || domain.endsWith('.' + allowedDomain)
      );
    } catch (e: unknown) {
      if (e instanceof Error) {
        console.warn(`URL mal formada: ${url}. Error: ${e.message}`);
      } else {
        console.warn(`URL mal formada: ${url}. Error desconocido`);
      }
      return false;
    }
  }

  private isAbsoluteUrl(url: string): boolean {
    return /^https?:\/\//i.test(url);
  }

  private isYoutubeUrl(url: string): boolean {
    try {
      const parsedUrl = new URL(url);
      return this.allowedDomains.some(domain => 
        parsedUrl.hostname.includes('youtube') || parsedUrl.hostname.includes('youtu.be')
      );
    } catch (e: unknown) {
      if (e instanceof Error) {
        console.warn(`URL de YouTube no válida: ${url}. Error: ${e.message}`);
      } else {
        console.warn(`URL de YouTube no válida: ${url}. Error desconocido`);
      }
      return false;
    }
  }

  private extractYoutubeId(url: string): string {
    try {
      const regExp = /^.*(youtu\.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/;
      const match = regExp.exec(url);
      return match && match[2].length === 11 ? match[2] : '';
    } catch (e: unknown) {
      if (e instanceof Error) {
        console.warn(`Error extrayendo ID de YouTube de URL: ${url}. Error: ${e.message}`);
      } else {
        console.warn(`Error extrayendo ID de YouTube de URL: ${url}. Error desconocido`);
      }
      return '';
    }
  }
}