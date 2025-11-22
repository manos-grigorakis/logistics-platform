import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UsersListResponse } from './models/users-list-response';
import { environment } from '../../environments/environment';
import { AuthService } from '../auth/services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  constructor() {}
  private http: HttpClient = inject(HttpClient);
  private authService: AuthService = inject(AuthService);
  private jwtToken?: string | null;

  public fetchUsers(): Observable<UsersListResponse[]> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    return this.http.get<UsersListResponse[]>(`${environment.apiUrl}/users`, { headers: headers });
  }
}
