import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SupplierResponse } from '../models/supplier-response.interface';
import { ApiResponse } from '../../../shared/models/api-response.interface';
import { environment } from '../../../../environments/environment';
import { SupplierRequest } from '../models/supplier-request.interface';
import { FetchSuppliersParams } from '../models/fetch-suppliers-params.interface';
import { addHttpParam } from '../../../shared/utils/add-http-params.util';
import { PagedResponse } from '../../../shared/models/paged-response.interface';
import { Supplier } from '../models/supplier.interface';

@Injectable({
  providedIn: 'root',
})
export class SuppliersService {
  private http = inject(HttpClient);

  constructor() {}

  public fetchSuppliers(
    param: FetchSuppliersParams = {},
  ): Observable<ApiResponse<PagedResponse<Supplier>>> {
    let params = new HttpParams();
    params = addHttpParam(params, 'page', param.page);
    params = addHttpParam(params, 'size', param.size);
    params = addHttpParam(params, 'sortBy', param.sortBy);
    params = addHttpParam(params, 'sortDirection', param.sortDirection);
    params = addHttpParam(params, 'companyName', param.companyName);

    return this.http.get<ApiResponse<PagedResponse<Supplier>>>(`${environment.apiUrl}/suppliers`, {
      params,
    });
  }

  public fetchSupplierById(id: number): Observable<ApiResponse<SupplierResponse>> {
    return this.http.get<ApiResponse<SupplierResponse>>(`${environment.apiUrl}/suppliers/${id}`);
  }

  // prettier-ignore
  public createSupplier(request: SupplierRequest): Observable<ApiResponse<SupplierResponse>> {
    return this.http.post<ApiResponse<SupplierResponse>>(`${environment.apiUrl}/suppliers`, request);
  }

  // prettier-ignore
  public updateSupplierById(id: number, request: SupplierRequest): Observable<ApiResponse<SupplierResponse>> {
    return this.http.put<ApiResponse<SupplierResponse>>(`${environment.apiUrl}/suppliers/${id}`, request);
  }

  // prettier-ignore
  public deactivateSupplierById(id: number): Observable<void> {
    return this.http.patch<void>(`${environment.apiUrl}/suppliers/${id}/deactivate`, null);
  }
}
