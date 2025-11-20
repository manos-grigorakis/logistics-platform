import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { LoginResponse } from '../models/login-response';
import { environment } from '../../../environments/environment';
import { User } from '../models/User';
import { LoginRequest } from '../models/login-request';
import { delay, Observable, Subscription, tap, of } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Router } from '@angular/router';
import { toast } from 'ngx-sonner';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private userData?: User;
  private tokenSubscription: Subscription = new Subscription();

  constructor(private jwtHelper: JwtHelperService) {
    // Initiallize auth state
    this.initiallizeAuth();
  }

  public authenticateUser(formData: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(environment.apiUrl + '/auth/login', formData).pipe(
      tap((res) => {
        if (res.token) {
          sessionStorage.setItem('jwtToken', res.token);
          sessionStorage.setItem('user', JSON.stringify(res.user));
          this.userData = res.user;

          // Set counter to logout user when token expires
          this.expirationCounter(res.token);
        }
      })
    );
  }

  public logout(): void {
    // Unsubscribe from previous timer
    this.tokenSubscription.unsubscribe();

    // Clear data
    this.userData = undefined;
    sessionStorage.removeItem('jwtToken');
    sessionStorage.removeItem('user');
  }

  public isAuthenticated(): boolean {
    if (this.getJwtToken() && !this.isJwtTokenExpired()) return true;
    return false;
  }

  public getJwtToken(): string | null {
    return sessionStorage.getItem('jwtToken');
  }

  public loadUserData(): User | undefined {
    const storedUser = sessionStorage.getItem('user');

    if (!storedUser) return undefined;

    this.userData = JSON.parse(storedUser);

    return this.userData;
  }

  // Helper methods
  // Initiallize auth state
  private initiallizeAuth(): void {
    const token = this.getJwtToken();

    if (!token) return;

    if (this.jwtHelper.isTokenExpired(token)) {
      this.logout();
      return;
    }

    this.loadUserDataFromStorage();
    this.expirationCounter(token);
  }

  private loadUserDataFromStorage(): void {
    const storedUser = sessionStorage.getItem('user');

    if (!storedUser) {
      this.logout();
      return;
    }

    this.userData = JSON.parse(storedUser);
  }

  // Set ups automatic logout when JWT token expires
  private expirationCounter(token: string): void {
    const expirationDate = this.jwtHelper.getTokenExpirationDate(token);

    if (!expirationDate) {
      this.logout();
      return;
    }

    const timeout = expirationDate.getTime() - Date.now();

    if (timeout <= 0) {
      this.logout();
      return;
    }

    // Unsubscribe from previous timer
    this.tokenSubscription.unsubscribe();

    this.tokenSubscription = of(null)
      .pipe(delay(timeout))
      .subscribe(() => {
        toast.info('Your session has expired. Please login again.');
        this.logout();
        this.router.navigate(['/login']);
      });
  }

  private isJwtTokenExpired(): boolean {
    const token = this.getJwtToken();
    if (!token) return true;

    return this.jwtHelper.isTokenExpired(token);
  }
}
