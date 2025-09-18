import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-meditation-player',
  standalone: true,
  imports: [MatIconModule, CommonModule],
  templateUrl: './meditation-player.component.html',
  styleUrls: ['./meditation-player.component.css']
})
export class MeditationPlayerComponent {
  @Input() meditation: any;
  @Output() close = new EventEmitter<void>();

  constructor(private sanitizer: DomSanitizer) {}

  get mediaType(): string {
    return this.meditation.type === 'audio' ? 'audio/mpeg' : 'video/mp4';
  }

  getSafeYoutubeUrl(url: string) {
    const videoId = this.extractYoutubeId(url);
    return this.sanitizer.bypassSecurityTrustResourceUrl(
      `https://www.youtube.com/embed/${videoId}?autoplay=1`
    );
  }

  private extractYoutubeId(url: string): string {
    const regExp = /^.*(youtu\.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
    const match = url.match(regExp);
    return match && match[2].length === 11 ? match[2] : '';
  }
}
