import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthService } from '../auth/services/auth.service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class MetadataService {
  public quoteStatuses$ = new BehaviorSubject<string[]>([]);
  public quoteItemUnits$ = new BehaviorSubject<string[]>([]);

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

  public fetchQuotesStatuses(): void {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    if (this.quoteStatuses$.value.length > 0) return;

    this.http
      .get<string[]>(`${environment.apiUrl}/metadata/quote-statuses`, { headers: headers })
      .subscribe({
        next: (res) => this.quoteStatuses$.next(res),
        error: (err) => console.error('Failed to fetch quote statuses', err),
      });
  }

  public fetchQuoteItemUnits(): void {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    if (this.quoteItemUnits$.value.length > 0) return;

    this.http
      .get<string[]>(`${environment.apiUrl}/metadata/quote-item-units`, { headers: headers })
      .subscribe({
        next: (res) => this.quoteItemUnits$.next(res),
        error: (err) => console.error('Failed to fetch quote units', err),
      });
  }
}
