import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { CommentService } from '../../../services/comment.service';
import { AuthService } from '../../../auth.service';

@Component({
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule
  ],
  templateUrl: './new-post.component.html',
  styleUrls: ['./new-post.component.css']
})
export class NewPostComponent {
  categories = ['Ansiedad', 'Depresión', 'Estrés', 'Meditación'];
  
  postForm = new FormGroup({
    title: new FormControl('', [Validators.required]),
    content: new FormControl('', [Validators.required]),
    category: new FormControl('', [Validators.required])
  });

  usuarioId: string | null = null;

  constructor(
    public dialogRef: MatDialogRef<NewPostComponent>,
    private readonly commentService: CommentService,
    private readonly authService: AuthService
  ) {
    const usuario = this.authService.getUsuario();
    this.usuarioId = usuario ? usuario.id : null;
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.postForm.valid && this.usuarioId) {
      this.commentService.postComment(
        this.usuarioId,
        this.postForm.value.title!,
        this.postForm.value.content!,
        this.postForm.value.category!
      ).subscribe({
        next: (response) => {
          this.dialogRef.close(response);
        },
        error: (error) => {
          console.error('Error al publicar:', error);
        }
      });
    } else {
      console.error('No hay usuario autenticado o el formulario es inválido');
    }
  }
}