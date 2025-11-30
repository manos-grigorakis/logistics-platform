import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/services/auth.service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class MetadataService {
  private http = inject(HttpClient);
  private authService: AuthService = inject(AuthService);
  private jwtToken?: string | null;

  public fetchCustomersTypes(): Observable<Array<string>> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    return this.http.get<Array<string>>(`${environment.apiUrl}/metadata/customer-types`, {
      headers: headers,
    });
  }
}
