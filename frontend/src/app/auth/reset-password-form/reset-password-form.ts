import { Component, inject, OnInit } from '@angular/core';
import { FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { ResetPasswordRequest } from '../models/reset-password-request';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-reset-password-form',
  imports: [PrimaryButton, LoadingSpinner, ReactiveFormsModule],
  templateUrl: './reset-password-form.html',
  styleUrl: './reset-password-form.css',
})
export class ResetPasswordForm implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private token!: string;
  private isTokenValid: boolean = false;
  isLoading: boolean = false;
  errorMessage!: string;

  authService: AuthService = inject(AuthService);

  newPassword = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });

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
          toast.error('This password reset link is invalid or has expired');
          this.router.navigate(['/login']);
        }
      },
      error: (err) => {
        if (err.status === 400 || err.status === 404) {
          toast.error('This password reset link is invalid or has already been used');
        } else if (err.status === 500) {
          toast.error('Internal server error. Please try again later');
        } else {
          toast.error('An error occured. Please try again');
        }
        this.router.navigate(['/login']);
      },
    });
  }

  public onSubmit(): void {
    if (!this.newPassword.valid) return;
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
        toast.success('You have successfully updated your password');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 400 || err.status === 404) {
          toast.error('This password reset link is invalid or has already been used');
          this.router.navigate(['/login']);
        } else if (err.status === 500) {
          this.errorMessage = 'Internal server error. Please try again later';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }
      },
    });
  }
}
