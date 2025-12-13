import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserResponse } from './models/user-response';
import { environment } from '../../environments/environment';
import { User } from './models/user';
import { UserRequest } from './models/user-request';

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  private http: HttpClient = inject(HttpClient);

  constructor() {}

  public fetchUsers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${environment.apiUrl}/users`);
  }

  public getUser(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${environment.apiUrl}/users/${id}`);
  }

  public createUser(data: UserRequest): Observable<User> {
    return this.http.post<User>(`${environment.apiUrl}/users`, data);
  }

  public updateUser(id: number, data: UserRequest): Observable<User> {
    return this.http.put<User>(`${environment.apiUrl}/users/${id}`, data);
  }

  public deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/users/${id}`);
  }
}
