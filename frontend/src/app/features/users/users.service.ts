import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserResponse } from './models/user-response';
import { environment } from '../../../environments/environment';
import { User } from './models/user';
import { UserRequest } from './models/user-request';
import { ApiResponse } from '../../shared/models/api-response.interface';

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  private http: HttpClient = inject(HttpClient);

  constructor() {}

  public fetchUsers(): Observable<ApiResponse<UserResponse[]>> {
    return this.http.get<ApiResponse<UserResponse[]>>(`${environment.apiUrl}/users`);
  }

  public getUser(id: number): Observable<ApiResponse<UserResponse>> {
    return this.http.get<ApiResponse<UserResponse>>(`${environment.apiUrl}/users/${id}`);
  }

  public createUser(data: UserRequest): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(`${environment.apiUrl}/users`, data);
  }

  public updateUser(id: number, data: UserRequest): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${environment.apiUrl}/users/${id}`, data);
  }

  public deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/users/${id}`);
  }
}
