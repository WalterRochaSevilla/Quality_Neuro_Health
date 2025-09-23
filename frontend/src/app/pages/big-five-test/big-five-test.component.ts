import { Component } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-big-five-test',
  standalone: true,
  imports: [CommonModule, FormsModule, NgFor, NgIf, RouterModule],
  templateUrl: './big-five-test.component.html',
  styleUrl: './big-five-test.component.css'
})
export class BigFiveTestComponent {
  questions = [
    { text: "Soy alguien que es extrovertido, sociable.", trait: "extraversion" },
    { text: "Prefiero estar solo en lugar de en grupos grandes.", trait: "extraversion", reverse: true },
    { text: "Soy organizado y sigo un plan.", trait: "conscientiousness" },
    { text: "Dejo cosas para último momento.", trait: "conscientiousness", reverse: true },
    { text: "Me preocupo fácilmente.", trait: "neuroticism" },
    { text: "Me mantengo tranquilo bajo presión.", trait: "neuroticism", reverse: true },
    { text: "Soy considerado y amable con los demás.", trait: "agreeableness" },
    { text: "Pongo mis intereses primero, sin importar los demás.", trait: "agreeableness", reverse: true },
    { text: "Tengo una gran imaginación y creatividad.", trait: "openness" },
    { text: "Prefiero rutinas y evito nuevas experiencias.", trait: "openness", reverse: true }
  ];

  scores: { [key: string]: number } = {
    extraversion: 0,
    conscientiousness: 0,
    neuroticism: 0,
    agreeableness: 0,
    openness: 0
  };

  responses: number[] = new Array(10).fill(3); // Valores iniciales en escala de 1 a 5
  completed = false;

  submitTest() {
    this.completed = true;
    this.calculateScores();
  }

  calculateScores() {
    const traitTotals: { [key: string]: number } = {
      extraversion: 0,
      conscientiousness: 0,
      neuroticism: 0,
      agreeableness: 0,
      openness: 0
    };

    for (let i = 0; i < this.questions.length; i++) {
      let value = this.responses[i];
      if (this.questions[i].reverse) {
        value = 6 - value; // Invertir el puntaje en preguntas negativas
      }
      traitTotals[this.questions[i].trait] += value;
    }

    // Normalizamos los valores a un rango de 2-10
    Object.keys(traitTotals).forEach(trait => {
      this.scores[trait] = traitTotals[trait];
    });
  }

  getInterpretation(trait: string): string {
    const score = this.scores[trait];
    if (score <= 4) return "Bajo";
    if (score <= 7) return "Medio";
    return "Alto";
  }

  updateResponse(index: number, event: any) {
    this.responses[index] = event.target.value;
  }

  getRangeStyle(value: number) {
    let backgroundColor = '';
    switch (value) {
      case 1:
        backgroundColor = 'red';
        break;
      case 2:
        backgroundColor = 'orange';
        break;
      case 3:
        backgroundColor = 'blue';
        break;
      case 4:
        backgroundColor = 'lightgreen';
        break;
      case 5:
        backgroundColor = 'darkgreen';
        break;
      default:
        backgroundColor = '#ccc'; // Color por defecto
    }
    return {
      background: `${backgroundColor} 0% 0% no-repeat, linear-gradient(to right, ${backgroundColor} ${value * 20}%, #ddd ${value * 20}%)`
      // 'background': `linear-gradient(to right, ${backgroundColor} 0%, ${backgroundColor} ${value * 20}%, #ddd ${value * 20}%, #ddd 100%)`
    };
  }
}
