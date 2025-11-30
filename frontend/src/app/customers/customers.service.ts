import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from '../auth/services/auth.service';
import { FetchCustomersResponse } from './models/fetch-customers-response';
import { FetchCustomersParameters } from './models/fetch-customers-parameters';

@Injectable({
  providedIn: 'root',
})
export class CustomersService {
  private http = inject(HttpClient);
  private authService: AuthService = inject(AuthService);
  private jwtToken?: string | null;

  public fetchCustomers(param: FetchCustomersParameters = {}): Observable<FetchCustomersResponse> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    let params = new HttpParams();
    params = this.addParam(params, 'page', param.page);
    params = this.addParam(params, 'size', param.size);
    params = this.addParam(params, 'sortBy', param.sortBy);
    params = this.addParam(params, 'sortDirection', param.sortDirection);
    params = this.addParam(params, 'tin', param.tin);
    params = this.addParam(params, 'companyName', param.companyName);
    params = this.addParam(params, 'customerType', param.customerType);

    return this.http.get<FetchCustomersResponse>(`${environment.apiUrl}/customers`, {
      headers: headers,
      params: params,
    });
  }

  // Helper method that creates param
  private addParam(param: HttpParams, key: string, value: any): HttpParams {
    if (value === undefined) return param;
    return param.set(key, value.toString());
  }
}
