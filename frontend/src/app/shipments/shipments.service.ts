import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ShipmentsResponse } from './models/shipments-response';
import { environment } from '../../environments/environment';
import { ShipmentParams } from './models/shipment-params';
import { addHttpParam } from '../shared/utils/add-http-params.util';

@Injectable({
  providedIn: 'root',
})
export class ShipmentsService {
  private http = inject(HttpClient);

  constructor() {}

  public fetchShipments(param: ShipmentParams = {}): Observable<ShipmentsResponse> {
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
      params.set('driverId', param.driverId);
    }

    return this.http.get<ShipmentsResponse>(`${environment.apiUrl}/shipments`, { params });
  }
}
