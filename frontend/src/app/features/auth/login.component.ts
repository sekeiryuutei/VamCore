import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { environment } from '../../../environments/environment';

/**
 * Pantalla de login. Llama a POST /api/v1/auth/login (ver
 * identity.infrastructure.web.AuthController en el backend) y, si es
 * exitoso, redirige al feature por defecto (inventory / VamStock).
 */
@Component({
  selector: 'vc-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  protected readonly platformName = environment.platformName;
  protected readonly companyName = environment.companyName;

  email = '';
  password = '';
  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  constructor(private auth: AuthService, private router: Router) {}

  submit(): void {
    this.errorMessage.set(null);
    this.loading.set(true);
    this.auth.login(this.email, this.password).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/resumen']);
      },
      error: (err) => {
        this.loading.set(false);
        const backendMessage = err?.error?.message;
        this.errorMessage.set(backendMessage ?? 'No se pudo iniciar sesión. Verifica tus credenciales.');
      },
    });
  }
}
