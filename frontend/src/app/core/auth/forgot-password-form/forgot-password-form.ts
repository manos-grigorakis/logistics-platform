import { Component, inject } from '@angular/core';
import { PrimaryButton } from '../../../shared/ui/primary-button/primary-button';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { RouterLink } from '@angular/router';
import { MainInput } from '../../../shared/components/forms/main-input/main-input';
import { TranslatePipe } from '@ngx-translate/core';
import { AuthLayout } from '../components/auth-layout/auth-layout';
import { handleHttpErrors } from '../../../shared/utils/handle-http-errors.util';

@Component({
  selector: 'app-forgot-password-form',
  imports: [PrimaryButton, ReactiveFormsModule, RouterLink, MainInput, TranslatePipe, AuthLayout],
  templateUrl: './forgot-password-form.html',
  styleUrl: './forgot-password-form.css',
})
export class ForgotPasswordForm {
  authService: AuthService = inject(AuthService);
  successMessage?: string;
  errorMessage: string | null = null;
  isLoading: boolean = false;

  form = new FormGroup({
    email: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
  });

  get email(): FormControl {
    return this.form.get('email') as FormControl;
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

    this.isLoading = true;

    this.authService.forgotPassword(this.email.value).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.successMessage = 'auth.forgot-password.success';
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = handleHttpErrors(err.status);

        // Clear errors
        setTimeout(() => {
          this.errorMessage = null;
        }, 5000);
      },
    });
  }
}
