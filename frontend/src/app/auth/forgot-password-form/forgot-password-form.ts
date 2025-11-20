import { Component, inject } from '@angular/core';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-forgot-password-form',
  imports: [PrimaryButton, ReactiveFormsModule],
  templateUrl: './forgot-password-form.html',
  styleUrl: './forgot-password-form.css',
})
export class ForgotPasswordForm {
  authService: AuthService = inject(AuthService);
  successMessage?: string;
  errorMessage?: string;

  email = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required, Validators.email],
  });

  public onSubmit(): void {
    if (!this.email.valid) return;

    this.authService.forgotPassword(this.email.value).subscribe({
      next: (res) => {
        if (res.status === 200) {
          this.successMessage = res.body.message;
        }
      },
      error: (err) => {
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
