import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ShipmentsResponse } from './models/shipments-response';
import { environment } from '../../../environments/environment';
import { ShipmentParams } from './models/shipment-params';
import { addHttpParam } from '../../shared/utils/add-http-params.util';
import { ShipmentPayload } from './models/shipment-payload';
import { Shipment } from './models/shipment';
import { CmrDocumentSummaryResponse } from './models/cmr-document-summary-response';
import { ApiResponse } from '../../shared/models/api-response.interface';

@Injectable({
  providedIn: 'root',
})
export class ShipmentsService {
  private http = inject(HttpClient);

  constructor() {}

  public fetchShipments(param: ShipmentParams = {}): Observable<ApiResponse<ShipmentsResponse>> {
    let params = new HttpParams();
    params = addHttpParam(params, 'page', param.page);
    params = addHttpParam(params, 'size', param.size);
    params = addHttpParam(params, 'sortBy', param.sortBy);
    params = addHttpParam(params, 'sortDirection', param.sortDirection);
    params = addHttpParam(params, 'number', param.number);
    params = addHttpParam(params, 'status', param.status);
    params = addHttpParam(params, 'pickupFrom', param.pickupFrom);
    params = addHttpParam(params, 'pickupTo', param.pickupTo);

    if (param.driverId !== undefined) {
      params = params.set('driverId', param.driverId);
    }

    if (param.customerId !== undefined) {
      params = params.set('customerId', param.customerId.toString());
    }

    return this.http.get<ApiResponse<ShipmentsResponse>>(`${environment.apiUrl}/shipments`, {
      params,
    });
  }

  public getShipment(id: number): Observable<ApiResponse<Shipment>> {
    return this.http.get<ApiResponse<Shipment>>(`${environment.apiUrl}/shipments/${id}`);
  }

  public createShipment(payload: ShipmentPayload): Observable<ApiResponse<ShipmentsResponse>> {
    return this.http.post<ApiResponse<ShipmentsResponse>>(
      `${environment.apiUrl}/shipments`,
      payload,
    );
  }

  // prettier-ignore
  public updateShipment(id: number, payload: ShipmentPayload): Observable<ApiResponse<ShipmentsResponse>> {
    return this.http.put<ApiResponse<ShipmentsResponse>>(
      `${environment.apiUrl}/shipments/${id}`,
      payload,
    );
  }

  public updateShipmentStatus(id: number, status: string): Observable<void> {
    return this.http.patch<void>(`${environment.apiUrl}/shipments/${id}/status`, {
      status: status,
    });
  }

  // prettier-ignore
  public getCmrDocumentByShipmentId(shipmentId: number): Observable<ApiResponse<CmrDocumentSummaryResponse>> {
    return this.http.get<ApiResponse<CmrDocumentSummaryResponse>>(
      `${environment.apiUrl}/shipments/${shipmentId}/cmr`,
    );
  }
}
