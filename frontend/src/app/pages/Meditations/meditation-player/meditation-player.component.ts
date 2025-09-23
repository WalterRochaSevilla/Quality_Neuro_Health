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
  @Output() playerClosed = new EventEmitter<void>();


  get mediaType(): string {
    if (this.meditation.type === 'audio') return 'audio/mpeg';
    if (this.meditation.type === 'video') return 'video/mp4';
    return '';
  }
//
  getSafeUrl(url: string, type: string): string {
    const pipe = new SafeUrlPipe();
    return pipe.transform(url, type);
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
