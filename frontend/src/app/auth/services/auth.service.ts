import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { LoginResponse } from '../models/login-response';
import { environment } from '../../../environments/environment';
import { User } from '../models/User';
import { LoginRequest } from '../models/login-request';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);

  private userData?: User;

  authenticateUser(formData: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(environment.apiUrl + '/auth/login', formData).pipe(
      tap((res) => {
        if (res.token) {
          sessionStorage.setItem('jwtToken', res.token);
          sessionStorage.setItem('user', JSON.stringify(res.user));
        }
      })
    );
  }

  logout(): void {
    sessionStorage.removeItem('jwtToken');
    sessionStorage.removeItem('user');
  }

  getJwtToken(): string | null {
    return sessionStorage.getItem('jwtToken');
  }

  loadUserData(): User | undefined {
    const storedUser = sessionStorage.getItem('user');

    if (!storedUser) return undefined;

    this.userData = JSON.parse(storedUser);

    return this.userData;
  }

  isAuthenticated(): boolean {
    return this.getJwtToken() ? true : false;
  }
}
