import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Role } from './models/role';
import { RoleRequest } from './models/role-request';
import { ApiResponse } from '../../shared/models/api-response.interface';

@Injectable({
  providedIn: 'root',
})
export class RolesService {
  private http: HttpClient = inject(HttpClient);

  constructor() {}

  public fetchRoles(): Observable<ApiResponse<Role[]>> {
    return this.http.get<ApiResponse<Role[]>>(`${environment.apiUrl}/roles`);
  }

  public fetchRole(id: number): Observable<ApiResponse<Role>> {
    return this.http.get<ApiResponse<Role>>(`${environment.apiUrl}/roles/${id}`);
  }

  public createRole(data: RoleRequest): Observable<ApiResponse<Role>> {
    return this.http.post<ApiResponse<Role>>(`${environment.apiUrl}/roles`, data);
  }

  public updateRole(id: number, data: RoleRequest): Observable<ApiResponse<Role>> {
    return this.http.put<ApiResponse<Role>>(`${environment.apiUrl}/roles/${id}`, data);
  }

  public deleteRole(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/roles/${id}`);
  }
}
