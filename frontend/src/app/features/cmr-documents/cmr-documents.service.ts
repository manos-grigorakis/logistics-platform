import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../shared/models/api-response.interface';
import { environment } from '../../../environments/environment';
import { UploadSignedCmrDocumentRequest } from './models/upload-signed-cmr-document-request.interface';
import { CmrDocument } from './models/cmr-document.interface';
import { CmrDocumentFilterRequest } from './models/cmr-document-filter-request.interface';
import { addHttpParam } from '../../shared/utils/add-http-params.util';
import { Page } from '../../shared/models/page.interface';

@Injectable({
  providedIn: 'root',
})
export class CmrDocumentsService {
  private http = inject(HttpClient);

  constructor() {}

  // prettier-ignore
  public fetchCmrDocuments(param: CmrDocumentFilterRequest = {}): Observable<ApiResponse<Page<CmrDocument>>> {
    let params = new HttpParams();

    params = addHttpParam(params, 'size', param.size);
    params = addHttpParam(params, 'page', param.page);
    params = addHttpParam(params, 'sortBy', param.sortBy);
    params = addHttpParam(params, 'sortDirection', param.sortDirection);
    params = addHttpParam(params, 'number', param.number);
    params = addHttpParam(params, 'status', param.status);

    return this.http.get<ApiResponse<Page<CmrDocument>>>(`${environment.apiUrl}/cmr-documents`, {
      params,
    });
  }

  public fetchCmrDocument(id: number): Observable<ApiResponse<CmrDocument>> {
    return this.http.get<ApiResponse<CmrDocument>>(`${environment.apiUrl}/cmr-documents/${id}`);
  }

  public updateCmrDocumentStatus(id: number, status: string): Observable<void> {
    return this.http.patch<void>(`${environment.apiUrl}/cmr-documents/${id}/status`, { status });
  }

  // prettier-ignore
  public uploadSignedCmrDocument(id: number, request: UploadSignedCmrDocumentRequest): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/cmr-documents/${id}/signed-copy`, request);
  }
}
