import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserResponse } from './models/user-response';
import { environment } from '../../environments/environment';
import { AuthService } from '../auth/services/auth.service';
import { User } from './models/user';
import { UserRequest } from './models/user-request';

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  constructor() {}
  private http: HttpClient = inject(HttpClient);
  private authService: AuthService = inject(AuthService);
  private jwtToken?: string | null;

  public fetchUsers(): Observable<UserResponse[]> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    return this.http.get<UserResponse[]>(`${environment.apiUrl}/users`, { headers: headers });
  }

  public getUser(id: number): Observable<UserResponse> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    return this.http.get<UserResponse>(`${environment.apiUrl}/users/${id}`, {
      headers: headers,
    });
  }

  public createUser(data: UserRequest): Observable<User> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    return this.http.post<User>(`${environment.apiUrl}/users`, data, { headers: headers });
  }

  public updateUser(id: number, data: UserRequest): Observable<User> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    return this.http.put<User>(`${environment.apiUrl}/users/${id}`, data, { headers: headers });
  }

  public deleteUser(id: number): Observable<void> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    return this.http.delete<void>(`${environment.apiUrl}/users/${id}`, { headers: headers });
  }
}
