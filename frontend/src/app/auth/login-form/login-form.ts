import { Component, inject } from '@angular/core';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { LoginRequest } from '../models/login-request';
import { Router, RouterLink } from '@angular/router';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { MainInput } from '../../shared/forms/main-input/main-input';
import { ErrorAlert } from '../../shared/ui/error-alert/error-alert';

@Component({
  selector: 'app-login-form',
  imports: [PrimaryButton, ReactiveFormsModule, LoadingSpinner, RouterLink, MainInput, ErrorAlert],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css',
})
export class LoginForm {
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);
  loginFailed: boolean = false;
  errorMessage?: string;
  isLoading: boolean = false;

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
        this.loginFailed = true;

        if (err.status === 401) {
          this.errorMessage = 'Invalid Credentials';
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again later';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }

        setTimeout(() => {
          this.loginFailed = false;
        }, 5000);
      },
    });
  }
}
