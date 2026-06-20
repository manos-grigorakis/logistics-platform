import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../shared/models/api-response.interface';
import { SupplierPayment } from '../models/supplier-payments.interface';
import { environment } from '../../../../environments/environment';
import { FetchSupplierPaymentsParams } from '../models/fetch-supplier-payments-params.interface';
import { addHttpParam } from '../../../shared/utils/add-http-params.util';
import { SupplierPaymentsCreateRequest } from '../models/supplier-payments-create-request.interface';
import { SupplierPaymentsUpdateRequest } from '../models/supplier-payments-update-request.interface';
import { SupplierPaymentsList } from '../models/supplier-payments-list.interface';

@Injectable({
  providedIn: 'root',
})
export class SupplierPaymentsService {
  private http = inject(HttpClient);

  constructor() {}

  public fetchSupplierPayments(
    param: FetchSupplierPaymentsParams = {},
  ): Observable<ApiResponse<SupplierPaymentsList>> {
    let params = new HttpParams();
    params = addHttpParam(params, 'page', param.page);
    params = addHttpParam(params, 'size', param.size);
    params = addHttpParam(params, 'sortBy', param.sortBy);
    params = addHttpParam(params, 'sortDirection', param.sortDirection);
    params = addHttpParam(params, 'number', param.number);

    return this.http.get<ApiResponse<SupplierPaymentsList>>(
      `${environment.apiUrl}/supplier-payments`,
      {
        params,
      },
    );
  }

  public fetchSupplierPaymentById(id: number): Observable<ApiResponse<SupplierPayment>> {
    return this.http.get<ApiResponse<SupplierPayment>>(
      `${environment.apiUrl}/supplier-payments/${id}`,
    );
  }

  public createSupplierPayment(
    request: SupplierPaymentsCreateRequest,
  ): Observable<ApiResponse<SupplierPayment>> {
    return this.http.post<ApiResponse<SupplierPayment>>(
      `${environment.apiUrl}/supplier-payments`,
      request,
    );
  }

  public updateSupplierPayment(
    id: number,
    request: SupplierPaymentsUpdateRequest,
  ): Observable<ApiResponse<SupplierPayment>> {
    return this.http.put<ApiResponse<SupplierPayment>>(
      `${environment.apiUrl}/supplier-payments/${id}`,
      request,
    );
  }

  public updateSupplierPaymentStatus(id: number, status: string): Observable<void> {
    return this.http.patch<void>(`${environment.apiUrl}/supplier-payments/${id}/status`, {
      status,
    });
  }
}
