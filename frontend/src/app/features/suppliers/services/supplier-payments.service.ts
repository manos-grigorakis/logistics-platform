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
import { PagedResponse } from '../../../shared/models/paged-response.interface';

@Injectable({
  providedIn: 'root',
})
export class SupplierPaymentsService {
  private http = inject(HttpClient);

  constructor() {}

  public fetchSupplierPayments(
    param: FetchSupplierPaymentsParams = {},
  ): Observable<ApiResponse<PagedResponse<SupplierPayment>>> {
    let params = new HttpParams();
    params = addHttpParam(params, 'page', param.page);
    params = addHttpParam(params, 'size', param.size);
    params = addHttpParam(params, 'sortBy', param.sortBy);
    params = addHttpParam(params, 'sortDirection', param.sortDirection);
    params = addHttpParam(params, 'number', param.number);

    return this.http.get<ApiResponse<PagedResponse<SupplierPayment>>>(
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
    let formData: FormData = new FormData();
    formData.append('title', request.title);
    formData.append('totalAmount', request.totalAmount.toString());
    formData.append('type', request.type);
    formData.append('supplierId', request.supplierId.toString());

    // Optional
    if (request.description !== null) formData.append('description', request.description);
    if (request.paidAmount !== null) formData.append('paidAmount', request.paidAmount.toString());
    if (request.invoiceFile !== null) formData.append('invoiceFile', request.invoiceFile);
    if (request.receiptFile !== null) formData.append('receiptFile', request.receiptFile);

    return this.http.post<ApiResponse<SupplierPayment>>(
      `${environment.apiUrl}/supplier-payments`,
      formData,
    );
  }

  public updateSupplierPayment(
    id: number,
    request: SupplierPaymentsUpdateRequest,
  ): Observable<ApiResponse<SupplierPayment>> {
    let formData: FormData = new FormData();
    formData.append('title', request.title);
    formData.append('totalAmount', request.totalAmount.toString());
    formData.append('type', request.type);

    // Optional
    if (request.description !== null) formData.append('description', request.description);
    if (request.paidAmount !== null) formData.append('paidAmount', request.paidAmount.toString());
    if (request.invoiceFile !== null) formData.append('invoiceFile', request.invoiceFile);
    if (request.receiptFile !== null) formData.append('receiptFile', request.receiptFile);

    return this.http.put<ApiResponse<SupplierPayment>>(
      `${environment.apiUrl}/supplier-payments/${id}`,
      formData,
    );
  }

  public updateSupplierPaymentStatus(id: number, status: string): Observable<void> {
    return this.http.patch<void>(`${environment.apiUrl}/supplier-payments/${id}/status`, {
      status,
    });
  }
}
