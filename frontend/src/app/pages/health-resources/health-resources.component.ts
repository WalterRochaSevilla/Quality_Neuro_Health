import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { SafeUrlPipe } from './safe-url.pipe';

@Component({
  selector: 'app-health-resources',
  standalone: true,
  imports: [CommonModule, RouterModule, SafeUrlPipe],
  templateUrl: './health-resources.component.html',
  styleUrls: ['./health-resources.component.css']
})
export class HealthResourcesComponent {
  selectedVideoUrl: string | null = null;

  constructor(private readonly router: Router) {}

  goToHome() {
    this.router.navigate(['/']);
  }

  articles = [
    {
      title: 'Trastorno de Ansiedad Generalizada',
      description: 'La ansiedad persistente y excesiva que interfiere con las actividades diarias. Síntomas comunes incluyen preocupación constante, irritabilidad y tensión muscular.',
      links: [
        {
          text: 'Guía de autoayuda',
          type: 'video',
          target: 'https://www.youtube.com/embed/34ZVrmJxEUo'
        },
        {
          text: 'Técnicas de relajación',
          type: 'route',
          target: '/meditations'
        },
        {
          text: 'Consigue ayuda',
          type: 'routeWithFragment',
          targetPath: '/',
          targetFragment: 'appointment-section'
        }
      ]
    },
    {
      title: 'Depresión Mayor',
      description: 'Trastorno del estado de ánimo que causa sentimientos persistentes de tristeza y pérdida de interés. Puede afectar el sueño, apetito y niveles de energía.',
      links: [
        {
          text: 'Señales de alerta',
          type: 'video',
          target: 'https://www.youtube.com/embed/c5sW6t3eyQA'
        },
        {
          text: 'Recursos de apoyo',
          type: 'route',
          target: '/community'
        },
        {
          text: 'Consigue ayuda',
          type: 'routeWithFragment',
          targetPath: '/',
          targetFragment: 'appointment-section'
        }
      ]
    },
    {
      title: 'Trastorno Obsesivo-Compulsivo (TOC)',
      description: 'Patrón de pensamientos no deseados (obsesiones) y comportamientos repetitivos (compulsiones). Suele incluir miedos irracionales y rituales para reducir la ansiedad.',
      links: [
        {
          text: 'Consigue ayuda',
          type: 'routeWithFragment',
          targetPath: '/',
          targetFragment: 'appointment-section'
        }
      ]
    }
  ];

  handleLinkClick(link: any) {
    if (link.type === 'video') {
      this.selectedVideoUrl = link.target;
    } else if (link.type === 'routeWithFragment') {
      this.router.navigate([link.targetPath], { fragment: link.targetFragment });
    }
  }

  openVideoModal(url: string) {
    this.selectedVideoUrl = url;
  }

  closeVideoModal() {
    this.selectedVideoUrl = null;
  }
}