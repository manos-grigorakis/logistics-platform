import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { VehicleResponse } from './models/vehicle-response';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { VehicleRequest } from './models/vehicle-request';
import { ApiResponse } from '../../shared/models/api-response.interface';

@Injectable({
  providedIn: 'root',
})
export class VehiclesService {
  private http = inject(HttpClient);

  constructor() {}

  public fetchAllVehicles(): Observable<ApiResponse<VehicleResponse[]>> {
    return this.http.get<ApiResponse<VehicleResponse[]>>(`${environment.apiUrl}/vehicles`);
  }

  public fetchVehicleById(id: number): Observable<ApiResponse<VehicleResponse>> {
    return this.http.get<ApiResponse<VehicleResponse>>(`${environment.apiUrl}/vehicles/${id}`);
  }

  public createVehicle(payload: VehicleRequest): Observable<ApiResponse<VehicleResponse>> {
    return this.http.post<ApiResponse<VehicleResponse>>(`${environment.apiUrl}/vehicles`, payload);
  }

  // prettier-ignore
  public updateVehicle(id: number, payload: VehicleRequest): Observable<ApiResponse<VehicleResponse>> {
    return this.http.put<ApiResponse<VehicleResponse>>(`${environment.apiUrl}/vehicles/${id}`, payload);
  }

  public deleteVehicleById(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/vehicles/${id}`);
  }
}
