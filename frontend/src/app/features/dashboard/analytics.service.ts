import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../shared/models/api-response.interface';
import { ValueResponse } from '../../shared/models/value-response';
import { environment } from '../../../environments/environment';
import { ValueByStatusResponse } from './models/value-by-status-response.interface';

@Injectable({
  providedIn: 'root',
})
export class AnalyticsService {
  private http = inject(HttpClient);

  public fetchTotalCustomers(): Observable<ApiResponse<ValueResponse<number>>> {
    return this.http.get<ApiResponse<ValueResponse<number>>>(
      `${environment.apiUrl}/analytics/total-customers`,
    );
  }

  public fetchTotalShipments(): Observable<ApiResponse<ValueResponse<number>>> {
    return this.http.get<ApiResponse<ValueResponse<number>>>(
      `${environment.apiUrl}/analytics/total-shipments`,
    );
  }

  public fetchTotalOutstandingAmount(): Observable<ApiResponse<ValueResponse<number>>> {
    return this.http.get<ApiResponse<ValueResponse<number>>>(
      `${environment.apiUrl}/analytics/total-outstanding-amount`,
    );
  }

  public fetchTotalPendingShipments(): Observable<ApiResponse<ValueResponse<number>>> {
    return this.http.get<ApiResponse<ValueResponse<number>>>(
      `${environment.apiUrl}/analytics/total-pending-shipments`,
    );
  }

  public fetchQuotesByStatus(): Observable<ApiResponse<ValueByStatusResponse[]>> {
    return this.http.get<ApiResponse<ValueByStatusResponse[]>>(
      `${environment.apiUrl}/analytics/quotes-by-status`,
    );
  }

  public fetchShipmentsByStatus(): Observable<ApiResponse<ValueByStatusResponse[]>> {
    return this.http.get<ApiResponse<ValueByStatusResponse[]>>(
      `${environment.apiUrl}/analytics/shipments-by-status`,
    );
  }

  public fetchInvoicesByStatus(): Observable<ApiResponse<ValueByStatusResponse[]>> {
    return this.http.get<ApiResponse<ValueByStatusResponse[]>>(
      `${environment.apiUrl}/analytics/invoices-by-status`,
    );
  }
}
