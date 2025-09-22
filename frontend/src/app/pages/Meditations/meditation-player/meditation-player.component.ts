import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { SafeUrlPipe } from '../../health-resources/safe-url.pipe';
@Component({
  selector: 'app-meditation-player',
  standalone: true,
  imports: [MatIconModule, CommonModule, SafeUrlPipe],
  templateUrl: './meditation-player.component.html',
  styleUrls: ['./meditation-player.component.css']
})
export class MeditationPlayerComponent {
  @Input() meditation: any;
  @Output() closed = new EventEmitter<void>(); 

  constructor(private readonly sanitizer: DomSanitizer) {}

  get mediaType(): string {
    return this.meditation.type === 'audio' ? 'audio/mpeg' : 'video/mp4';
  }
//
  getSafeYoutubeUrl(url: string) {
    const videoId = this.extractYoutubeId(url);
    return this.sanitizer.bypassSecurityTrustResourceUrl(
      `https://www.youtube.com/embed/${videoId}?autoplay=1`
    );
  }

  // Método para verificar si hay subtítulos disponibles
  hasSubtitles(): boolean {
    return !!(this.meditation.subtitles || this.meditation.subtitlesEn);
  }

  // Método para verificar características de accesibilidad
  hasAccessibilityFeatures(): boolean {
    return !!(this.meditation.subtitles || 
              this.meditation.subtitlesEn || 
              this.meditation.descriptionTrack || 
              this.meditation.chapters);
  }

  private extractYoutubeId(url: string): string {
    const regExp = /^.*(youtu\.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/;
    const match = regExp.exec(url);
    return match && match[2].length === 11 ? match[2] : '';
}
}
