import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/services/auth.service';
import { environment } from '../../environments/environment';
import { Role } from './models/role';

@Injectable({
  providedIn: 'root',
})
export class RolesService {
  private authService: AuthService = inject(AuthService);
  private http: HttpClient = inject(HttpClient);
  private jwtToken?: string | null;

  constructor() {}

  public fetchRoles(): Observable<Role[]> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    return this.http.get<Role[]>(`${environment.apiUrl}/roles`, { headers: headers });
  }

  public deleteRole(id: number): Observable<void> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    return this.http.delete<void>(`${environment.apiUrl}/roles/${id}`, { headers: headers });
  }
}
