import { Component, inject, Injectable } from '@angular/core';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-login-form',
  imports: [PrimaryButton, ReactiveFormsModule],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css',
})
export class LoginForm {
  private http = inject(HttpClient);

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl(null, Validators.required),
  });

  get email(): FormControl {
    return this.loginForm.get('email') as FormControl;
  }

  get password(): FormControl {
    return this.loginForm.get('password') as FormControl;
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.http.post(environment.apiUrl + '/auth/login', this.loginForm.value).subscribe({
      next: (res) => {
        console.log('Login response: ', res);
        alert('Login successfully');
      },
      error: (err) => {
        console.error('Login failed', err);
        alert('Login failed');
      },
    });
  }
}
