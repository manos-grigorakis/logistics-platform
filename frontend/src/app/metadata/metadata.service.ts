import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class MetadataService {
  public quoteStatuses$ = new BehaviorSubject<string[]>([]);
  public quoteItemUnits$ = new BehaviorSubject<string[]>([]);

  private http = inject(HttpClient);

  public fetchCustomersTypes(): Observable<Array<string>> {
    return this.http.get<Array<string>>(`${environment.apiUrl}/metadata/customer-types`);
  }

  public fetchQuotesStatuses(): void {
    if (this.quoteStatuses$.value.length > 0) return;

    this.http.get<string[]>(`${environment.apiUrl}/metadata/quote-statuses`).subscribe({
      next: (res) => this.quoteStatuses$.next(res),
      error: (err) => console.error('Failed to fetch quote statuses', err),
    });
  }

  public fetchQuoteItemUnits(): void {
    if (this.quoteItemUnits$.value.length > 0) return;

    this.http.get<string[]>(`${environment.apiUrl}/metadata/quote-item-units`).subscribe({
      next: (res) => this.quoteItemUnits$.next(res),
      error: (err) => console.error('Failed to fetch quote units', err),
    });
  }
}
