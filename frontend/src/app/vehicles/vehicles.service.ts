import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { VehicleResponse } from './models/vehicle-response';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { VehicleRequest } from './models/vehicle-request';

@Injectable({
  providedIn: 'root',
})
export class VehiclesService {
  private http = inject(HttpClient);

  constructor() {}

  public fetchAllVehicles(): Observable<VehicleResponse[]> {
    return this.http.get<VehicleResponse[]>(`${environment.apiUrl}/vehicles`);
  }

  public fetchVehicleById(id: number): Observable<VehicleResponse> {
    return this.http.get<VehicleResponse>(`${environment.apiUrl}/vehicles/${id}`);
  }

  public createVehicle(payload: VehicleRequest): Observable<VehicleResponse> {
    return this.http.post<VehicleResponse>(`${environment.apiUrl}/vehicles`, payload);
  }

  public updateVehicle(id: number, payload: VehicleRequest): Observable<VehicleResponse> {
    return this.http.put<VehicleResponse>(`${environment.apiUrl}/vehicles/${id}`, payload);
  }

  public deleteVehicleById(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/vehicles/${id}`);
  }
}
