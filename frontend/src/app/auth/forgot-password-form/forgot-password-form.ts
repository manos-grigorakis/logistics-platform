import { Component, inject } from '@angular/core';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { FormControl, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { RouterLink } from '@angular/router';
import { MainInput } from '../../shared/forms/main-input/main-input';

@Component({
  selector: 'app-forgot-password-form',
  imports: [PrimaryButton, ReactiveFormsModule, LoadingSpinner, RouterLink, MainInput],
  templateUrl: './forgot-password-form.html',
  styleUrl: './forgot-password-form.css',
})
export class ForgotPasswordForm {
  authService: AuthService = inject(AuthService);
  successMessage?: string;
  errorMessage?: string;
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

        if (res.status === 200) {
          this.successMessage = res.body.message;
        }
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again later';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }

        // Clear errors
        setTimeout(() => {
          this.errorMessage = undefined;
        }, 5000);
      },
    });
  }
}
