import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { AuthService } from '../../auth.service';
import { Router } from '@angular/router';

export const passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const password = control.get('password');
  const confirmPassword = control.get('confirmPassword');

  return password && confirmPassword && password.value !== confirmPassword.value ? { passwordMismatch: true } : null;
};

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css'
})

export class SignupComponent {
  signupForm: FormGroup;
  hidePassword = true;
  hideConfirmPassword = true;

  constructor(private authService: AuthService, private router: Router) {
    this.signupForm = new FormGroup({
      nombre: new FormControl('', Validators.required),
      apellido: new FormControl('', Validators.required),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
      confirmPassword: new FormControl('', Validators.required)
    }, { validators: passwordMatchValidator });
  }
  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }

  toggleConfirmPasswordVisibility() {
    this.hideConfirmPassword = !this.hideConfirmPassword;
  }
  
  get nombre() {
    return this.signupForm.get('nombre');
  }

  get apellido() {
    return this.signupForm.get('apellido');
  }

  get email() {
    return this.signupForm.get('email');
  }

  get password() {
    return this.signupForm.get('password');
  }

  get confirmPassword() {
    return this.signupForm.get('confirmPassword');
  }


  onSubmit() {
    if (this.signupForm.valid && this.password?.value === this.confirmPassword?.value) {
      const { nombre, apellido, email, password } = this.signupForm.value;
  
      this.authService.signup(nombre, apellido, email, password).subscribe({
        next: (response) => {
          console.log('Usuario registrado con éxito:', response);
          alert('Usuario registrado con éxito');
          this.router.navigate(['/login']);
        },
        error: (error) => {
          console.error('Error al registrar:', error);
          alert('Error al registrar el usuario');
        }
      });
    } else {
      console.log('Formulario inválido');
    }
  }
  
}