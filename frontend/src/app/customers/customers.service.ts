import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from '../auth/services/auth.service';
import { FetchCustomersResponse } from './models/fetch-customers-response';
import { CustomerType } from './models/customer-type';

@Injectable({
  providedIn: 'root',
})
export class CustomersService {
  private http = inject(HttpClient);
  private authService: AuthService = inject(AuthService);
  private jwtToken?: string | null;

  public fetchCustomers(
    page?: number,
    size?: number,
    sortBy?: string,
    sortDirection?: string,
    customerType?: CustomerType,
  ): Observable<FetchCustomersResponse> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    let params = new HttpParams();

    if (page !== undefined) {
      params = params.set('page', page.toString());
    }

    if (size !== undefined) {
      params = params.set('size', size.toString());
    }

    if (sortBy !== undefined) {
      params = params.set('sortBy', sortBy);
    }

    if (sortDirection !== undefined) {
      params = params.set('sortDirection', sortDirection);
    }

    if (customerType !== undefined) {
      params = params.set('customerType', customerType.toString());
    }

    return this.http.get<FetchCustomersResponse>(`${environment.apiUrl}/customers`, {
      headers: headers,
      params: params,
    });
  }
}
