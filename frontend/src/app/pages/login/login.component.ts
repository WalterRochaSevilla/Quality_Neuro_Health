import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  contrasena: string = '';
  errorMessage: string = '';

  constructor(private readonly authService: AuthService, private readonly router: Router) {}

  onSubmit() {
    this.authService.login(this.email, this.contrasena).subscribe({
      next: (response) => {
        console.log('Login exitoso:', response);
        this.authService.guardarUsuario(response);
        
        // Redirigir según el rol
        if (response.rol === 'administrador') {
          this.router.navigate(['/admin']); 
        } else {
          this.router.navigate(['/home']); 
        }
      },
      error: (error) => {
        if (error.status === 401) {
          this.errorMessage = 'Credenciales incorrectas. Por favor, intente de nuevo.';
        } else {
          this.errorMessage = 'Hubo un error. Intente nuevamente más tarde.';
        }
      }
    });
  }

  onGoogleLogin() {
    console.log('Login con Google');
  }

  mailIcon: string = 'mail';
  changeIcon(isHovered: boolean) {
    this.mailIcon = isHovered ? 'mark_email_read' : 'mail';
  }
}