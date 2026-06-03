import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { LoginResponse } from '../models/login-response';
import { environment } from '../../../environments/environment';
import { User } from '../models/User';
import { LoginRequest } from '../models/login-request';
import { delay, Observable, Subscription, tap, of, map } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Router } from '@angular/router';
import { toast } from 'ngx-sonner';
import { ValidateResetPasswordTokenResponse } from '../models/validate-reset-password-token-response';
import { ResetPasswordRequest } from '../models/reset-password-request';
import { SetupPasswordRequest } from '../models/setup-password-request';
import { SKIP_AUTH } from '../interceptors/auth-http.context';
import { JwtPayload } from '../models/jwt-payload';
import { ApiResponse } from '../../shared/models/api-response.interface';
import { MessageResponse } from '../../shared/models/message-response.interface';

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

  public authenticateUser(formData: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http
      .post<ApiResponse<LoginResponse>>(environment.apiUrl + '/auth/login', formData, {
        context: new HttpContext().set(SKIP_AUTH, true),
      })
      .pipe(
        tap((res) => {
          const data = res.data;

          if (data.token) {
            sessionStorage.setItem('jwtToken', data.token);
            sessionStorage.setItem('user', JSON.stringify(data.user));
            this.userData = data.user;

            // Set counter to logout user when token expires
            this.expirationCounter(data.token);
          }
        }),
      );
  }

  public forgotPassword(email: string): Observable<ApiResponse<MessageResponse>> {
    return this.http.post<ApiResponse<MessageResponse>>(
      environment.apiUrl + '/auth/request-reset',
      { email },
      { context: new HttpContext().set(SKIP_AUTH, true) },
    );
  }

  public validateResetPasswordToken(token: string): Observable<boolean> {
    return this.http
      .get<ApiResponse<ValidateResetPasswordTokenResponse>>(
        `${environment.apiUrl}/auth/reset-password?token=${token}`,
        {
          context: new HttpContext().set(SKIP_AUTH, true),
        },
      )
      .pipe(map((res) => res.data.valid));
  }

  public resetPassword(data: ResetPasswordRequest): Observable<ApiResponse<MessageResponse>> {
    return this.http.post<ApiResponse<MessageResponse>>(
      `${environment.apiUrl}/auth/reset-password/confirm`,
      data,
      {
        context: new HttpContext().set(SKIP_AUTH, true),
      },
    );
  }

  public setupPassword(data: SetupPasswordRequest): Observable<ApiResponse<MessageResponse>> {
    return this.http.post<ApiResponse<MessageResponse>>(
      `${environment.apiUrl}/auth/setup-password`,
      data,
      {
        context: new HttpContext().set(SKIP_AUTH, true),
      },
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

  public getUserRole(): string {
    const token = this.getJwtToken();

    if (!token || this.jwtHelper.isTokenExpired(token)) return '';

    const decodedToken = this.jwtHelper.decodeToken(token);

    return decodedToken.role || '';
  }

  public isAdmin(): boolean {
    if (this.getUserRole() === 'ADMIN') return true;
    return false;
  }

  public getUserId(): number | null {
    const token = this.getJwtToken();
    if (!token) return null;

    let decoded: JwtPayload = this.jwtHelper.decodeToken(token) as JwtPayload;
    const sub = decoded.sub;

    if (!sub) return null;

    const id = parseInt(sub);
    return isNaN(id) ? null : id;
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
