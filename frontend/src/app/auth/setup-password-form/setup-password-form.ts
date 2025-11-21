import { Component, inject, OnInit } from '@angular/core';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { AuthService } from '../services/auth.service';
import {
  FormControl,
  FormGroup,
  ɵInternalFormsSharedModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SetupPasswordRequest } from '../models/setup-password-request';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-setup-password-form',
  imports: [PrimaryButton, LoadingSpinner, ɵInternalFormsSharedModule, ReactiveFormsModule],
  templateUrl: './setup-password-form.html',
  styleUrl: './setup-password-form.css',
})
export class SetupPasswordForm implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private token!: string;

  isLoading: boolean = false;
  errorMessage?: string;

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
      this.form.markAllAsTouched();
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
        toast.success('You have successfully setup you password');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 400 || err.status === 404) {
          toast.error('This password setup link is invalid or has already been used');
          this.router.navigate(['/login']);
        } else if (err.status === 500) {
          this.errorMessage = 'Internal server error. Please try again later';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }

        setTimeout(() => {
          this.errorMessage = undefined;
        }, 5000);
      },
    });
  }
}
