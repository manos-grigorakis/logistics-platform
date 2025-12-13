import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from '../auth/services/auth.service';
import { FetchCustomersResponse } from './models/fetch-customers-response';
import { FetchCustomersParameters } from './models/fetch-customers-parameters';
import { Customer } from './models/customer';
import { CustomerRequest } from './models/customer-request';

@Injectable({
  providedIn: 'root',
})
export class CustomersService {
  private http = inject(HttpClient);
  private jwtToken?: string | null;

  public fetchCustomers(param: FetchCustomersParameters = {}): Observable<FetchCustomersResponse> {
    let params = new HttpParams();
    params = this.addParam(params, 'page', param.page);
    params = this.addParam(params, 'size', param.size);
    params = this.addParam(params, 'sortBy', param.sortBy);
    params = this.addParam(params, 'sortDirection', param.sortDirection);
    params = this.addParam(params, 'tin', param.tin);
    params = this.addParam(params, 'companyName', param.companyName);
    params = this.addParam(params, 'customerType', param.customerType);

    return this.http.get<FetchCustomersResponse>(`${environment.apiUrl}/customers`, {
      params: params,
    });
  }

  public fetchCustomer(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${environment.apiUrl}/customers/${id}`);
  }

  public createCustomer(data: CustomerRequest): Observable<Customer> {
    return this.http.post<Customer>(`${environment.apiUrl}/customers`, data);
  }

  public updateCustomer(id: number, data: CustomerRequest): Observable<Customer> {
    return this.http.put<Customer>(`${environment.apiUrl}/customers/${id}`, data);
  }

  public deleteCustomer(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/customers/${id}`);
  }

  // Helper method that creates param
  private addParam(param: HttpParams, key: string, value: any): HttpParams {
    if (value === undefined) return param;
    return param.set(key, value.toString());
  }
}
