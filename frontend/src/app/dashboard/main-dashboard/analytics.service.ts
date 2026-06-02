import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { ValueResponse } from '../../shared/models/value-response';

@Injectable({
  providedIn: 'root',
})
export class AnalyticsService {
  private http = inject(HttpClient);

  public fetchTotalCustomers(): Observable<ValueResponse<number>> {
    return this.http.get<ValueResponse<number>>(`${environment.apiUrl}/analytics/total-customers`);
  }

  public fetchTotalShipments(): Observable<ValueResponse<number>> {
    return this.http.get<ValueResponse<number>>(`${environment.apiUrl}/analytics/total-shipments`);
  }

  public fetchTotalOutstandingAmount(): Observable<ValueResponse<number>> {
    return this.http.get<ValueResponse<number>>(
      `${environment.apiUrl}/analytics/total-outstanding-amount`,
    );
  }

  public fetchTotalPendingShipments(): Observable<ValueResponse<number>> {
    return this.http.get<ValueResponse<number>>(
      `${environment.apiUrl}/analytics/total-pending-shipments`,
    );
  }
}
