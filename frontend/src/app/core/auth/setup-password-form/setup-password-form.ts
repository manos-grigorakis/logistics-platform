import { Component, inject, OnInit } from '@angular/core';
import { PrimaryButton } from '../../../shared/ui/primary-button/primary-button';
import { AuthService } from '../services/auth.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SetupPasswordRequest } from '../models/setup-password-request';
import { MainInput } from '../../../shared/components/forms/main-input/main-input';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../services/language.service';
import { AuthLayout } from '../components/auth-layout/auth-layout';
import { handleHttpErrors } from '../../../shared/utils/handle-http-errors.util';

@Component({
  selector: 'app-setup-password-form',
  imports: [PrimaryButton, ReactiveFormsModule, MainInput, TranslatePipe, AuthLayout],
  templateUrl: './setup-password-form.html',
  styleUrl: './setup-password-form.css',
})
export class SetupPasswordForm implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private languageService = inject(LanguageService);
  private token!: string;

  isLoading: boolean = false;
  errorMessage: string | null = null;

  authService: AuthService = inject(AuthService);

  form = new FormGroup({
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  get password(): FormControl {
    return this.form.get('password') as FormControl;
  }

  ngOnInit(): void {
    const tokenParameter = this.route.snapshot.queryParamMap.get('token');

    if (!tokenParameter) {
      this.router.navigate(['/login']);
      return;
    }

    this.token = tokenParameter;
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

    const data: SetupPasswordRequest = {
      token: this.token,
      password: this.password.value,
    };

    this.authService.setupPassword(data).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.languageService.toastSuccess('auth.setup-password.success');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 400 || err.status === 404) {
          this.languageService.toastError('auth.setup-password.errors.invalid-or-used-link');
          this.router.navigate(['/login']);
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
