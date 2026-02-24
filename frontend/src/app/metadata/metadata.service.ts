import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ShipmentStatus } from '../shipments/models/shipment-status';

@Injectable({
  providedIn: 'root',
})
export class MetadataService {
  private http = inject(HttpClient);
  private shipmentStatusesSubject$ = new BehaviorSubject<ShipmentStatus[]>([]);

  public quoteStatuses$ = new BehaviorSubject<string[]>([]);
  public quoteItemUnits$ = new BehaviorSubject<string[]>([]);
  public shipmentStatuses$ = this.shipmentStatusesSubject$.asObservable();

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

  public fetchShipmentsStatuses(): Observable<ShipmentStatus[]> {
    if (this.shipmentStatusesSubject$.value.length > 0) return this.shipmentStatuses$;

    this.http.get<ShipmentStatus[]>(`${environment.apiUrl}/metadata/shipment-statuses`).subscribe({
      next: (res) => this.shipmentStatusesSubject$.next(res),
      error: (err) => console.error(err),
    });

    return this.shipmentStatuses$;
  }
}
