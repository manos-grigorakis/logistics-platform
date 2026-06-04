import { Component, inject, OnInit } from '@angular/core';
import { FormControl, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { ResetPasswordRequest } from '../models/reset-password-request';
import { MainInput } from '../../shared/forms/main-input/main-input';
import { ErrorAlert } from '../../shared/ui/error-alert/error-alert';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../shared/services/language.service';

@Component({
  selector: 'app-reset-password-form',
  imports: [
    PrimaryButton,
    LoadingSpinner,
    ReactiveFormsModule,
    MainInput,
    ErrorAlert,
    TranslatePipe,
  ],
  templateUrl: './reset-password-form.html',
  styleUrl: './reset-password-form.css',
})
export class ResetPasswordForm implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private languageService = inject(LanguageService);

  private token!: string;
  private isTokenValid: boolean = false;
  isLoading: boolean = false;
  errorMessage!: string;

  authService: AuthService = inject(AuthService);

  form = new FormGroup({
    newPassword: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  get newPassword(): FormControl {
    return this.form.get('newPassword') as FormControl;
  }

  ngOnInit(): void {
    const tokenParameter = this.route.snapshot.queryParamMap.get('token');

    if (!tokenParameter) {
      this.router.navigate(['/login']);
      return;
    }

    this.token = tokenParameter;

    // Validates token
    this.authService.validateResetPasswordToken(this.token).subscribe({
      next: (isValid) => {
        this.isTokenValid = isValid;

        if (!isValid) {
          this.languageService.toastError('auth.reset-password.errors.invalid-or-expired-link');
          this.router.navigate(['/login']);
        }
      },
      error: (err) => {
        if (err.status === 400 || err.status === 404) {
          this.languageService.toastError('auth.reset-password.errors.invalid-or-used-link');
        } else if (err.status === 500) {
          this.languageService.toastError('common.errors.server');
        } else {
          this.languageService.toastError('common.errors.generic');
        }
        this.router.navigate(['/login']);
      },
    });
  }

  public onSubmit(): void {
    if (this.form.invalid) {
      // Mark all controls as touched & re-run validators
      Object.values(this.form.controls).forEach((control) => {
        control.markAsTouched();
        control.updateValueAndValidity({ emitEvent: true });
      });
      return;
    }

    if (!this.isTokenValid) return;

    this.isLoading = true;

    // Map data to request interface
    const data: ResetPasswordRequest = {
      token: this.token,
      newPassword: this.newPassword.value,
    };

    this.authService.resetPassword(data).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.languageService.toastSuccess('auth.reset-password.success');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 400 || err.status === 404) {
          this.languageService.toastError('auth.reset-password.errors.invalid-or-used-link');
          this.router.navigate(['/login']);
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }
}
