import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { ValueResponse } from '../../shared/models/value-response';
import { ValueByStatusResponse } from '../models/value-by-status-response.interface';

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

  public fetchQuotesByStatus(): Observable<ValueByStatusResponse[]> {
    return this.http.get<ValueByStatusResponse[]>(
      `${environment.apiUrl}/analytics/quotes-by-status`,
    );
  }

  public fetchShipmentsByStatus(): Observable<ValueByStatusResponse[]> {
    return this.http.get<ValueByStatusResponse[]>(
      `${environment.apiUrl}/analytics/shipments-by-status`,
    );
  }

  public fetchInvoicesByStatus(): Observable<ValueByStatusResponse[]> {
    return this.http.get<ValueByStatusResponse[]>(
      `${environment.apiUrl}/analytics/invoices-by-status`,
    );
  }
}
