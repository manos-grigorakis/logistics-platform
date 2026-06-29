import { Component, inject } from '@angular/core';
import { PrimaryButton } from '../../../shared/ui/primary-button/primary-button';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { LoginRequest } from '../models/login-request';
import { Router, RouterLink } from '@angular/router';
import { MainInput } from '../../../shared/components/forms/main-input/main-input';
import { TranslatePipe } from '@ngx-translate/core';
import { AuthLayout } from '../components/auth-layout/auth-layout';
import { handleHttpErrors } from '../../../shared/utils/handle-http-errors.util';

@Component({
  selector: 'app-login-form',
  imports: [PrimaryButton, ReactiveFormsModule, RouterLink, MainInput, TranslatePipe, AuthLayout],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css',
})
export class LoginForm {
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);

  isLoading: boolean = false;
  errorMessage: string | null = null;

  authService: AuthService = inject(AuthService);

  loginForm = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  get email(): FormControl {
    return this.loginForm.get('email') as FormControl;
  }

  get password(): FormControl {
    return this.loginForm.get('password') as FormControl;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      // Mark all controls as touched & re-run validators
      Object.values(this.loginForm.controls).forEach((control) => {
        control.markAsTouched();
        control.updateValueAndValidity({ emitEvent: true });
      });
      return;
    }

    this.isLoading = true;
    const credentials: LoginRequest = this.loginForm.getRawValue();

    this.authService.authenticateUser(credentials).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 401) {
          this.errorMessage = 'auth.login.errors.invalid-credentials';
        } else {
          this.errorMessage = handleHttpErrors(err.status);
        }

        setTimeout(() => {
          this.errorMessage = null;
        }, 5000);
      },
    });
  }
}
