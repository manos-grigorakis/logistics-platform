import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ReconciliationProcessRequest } from './models/reconciliaton-process-request';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ReconciliationProcessResponse } from './models/reconciliation-process-response';

@Injectable({
  providedIn: 'root',
})
export class PaymentsService {
  private httpClient = inject(HttpClient);

  constructor() {}

  // prettier-ignore
  public reconciliationProcess(payload: ReconciliationProcessRequest): Observable<ReconciliationProcessResponse> {
    const formData = new FormData();
    formData.append('customerId', payload.customerId.toString());
    formData.append('invoiceFile', payload.invoiceFile);
    formData.append('bankStatement', payload.bankStatement);

    return this.httpClient.post<ReconciliationProcessResponse>(`${environment.apiUrl}/reconciliation/process`, formData);
  }
}
