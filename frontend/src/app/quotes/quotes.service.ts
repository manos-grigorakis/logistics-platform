import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from '../auth/services/auth.service';
import { Observable } from 'rxjs';
import { FetchQuotesResponse } from './models/fetch-quotes-response';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class QuotesService {
  private http: HttpClient = inject(HttpClient);
  private authService: AuthService = inject(AuthService);
  private jwtToken?: string | null;

  public fetchQuotes(): Observable<FetchQuotesResponse> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    return this.http.get<FetchQuotesResponse>(`${environment.apiUrl}/quotes`, {
      headers: headers,
    });
  }
}
