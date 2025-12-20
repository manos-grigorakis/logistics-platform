import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { environment } from '../../environments/environment';
import { FetchCustomersResponse } from './models/fetch-customers-response';
import { FetchCustomersParameters } from './models/fetch-customers-parameters';
import { Customer } from './models/customer';
import { CustomerRequest } from './models/customer-request';
import { QuotePerCustomerResponse } from './models/quote-per-customer-response';
import { QuotesPerCustomerParameters } from './models/quotes-per-customer-parameters';

@Injectable({
  providedIn: 'root',
})
export class CustomersService {
  private selectedCustomer = new BehaviorSubject<Customer | null>(null);
  public selectedCustomer$ = this.selectedCustomer.asObservable();

  private http = inject(HttpClient);

  setSelectedCustomer(c: Customer | null) {
    this.selectedCustomer.next(c);
  }

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

  public quotesPerCustomer(
    id: number,
    param: QuotesPerCustomerParameters = {},
  ): Observable<QuotePerCustomerResponse> {
    let params = new HttpParams();

    params = this.addParam(params, 'page', param.page);
    params = this.addParam(params, 'size', param.size);
    params = this.addParam(params, 'sortBy', param.sortBy);
    params = this.addParam(params, 'sortDirection', param.sortDirection);
    params = this.addParam(params, 'number', param.number);
    params = this.addParam(params, 'quoteStatus', param.quoteStatus);

    return this.http.get<QuotePerCustomerResponse>(`${environment.apiUrl}/customers/${id}/quotes`, {
      params,
    });
  }

  // Helper method that creates param
  private addParam(param: HttpParams, key: string, value: any): HttpParams {
    if (value === undefined) return param;
    return param.set(key, value.toString());
  }
}
