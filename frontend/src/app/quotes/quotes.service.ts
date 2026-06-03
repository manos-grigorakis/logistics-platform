import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FetchQuotesResponse } from './models/fetch-quotes-response';
import { environment } from '../../environments/environment';
import { QuoteResponse } from './models/quote-response';
import { FetchQuotesParameters } from './models/fetch-quotes-parameters';
import { QuoteRequest } from './models/quote-request';
import { CreatedQuoteResponse } from './models/created-quote-response';
import { ApiResponse } from '../shared/models/api-response.interface';

@Injectable({
  providedIn: 'root',
})
export class QuotesService {
  private http: HttpClient = inject(HttpClient);

  // prettier-ignore
  public fetchQuotes(param: FetchQuotesParameters = {}): Observable<ApiResponse<FetchQuotesResponse>> {
    let params = new HttpParams();
    params = this.addParam(params, 'page', param.page);
    params = this.addParam(params, 'size', param.size);
    params = this.addParam(params, 'sortBy', param.sortBy);
    params = this.addParam(params, 'sortDirection', param.sortDirection);
    params = this.addParam(params, 'number', param.number);
    params = this.addParam(params, 'companyName', param.companyName);
    params = this.addParam(params, 'quoteStatus', param.quoteStatus);

    return this.http.get<ApiResponse<FetchQuotesResponse>>(`${environment.apiUrl}/quotes`, {
      params: params,
    });
  }

  public fetchQuoteById(id: number): Observable<ApiResponse<QuoteResponse>> {
    return this.http.get<ApiResponse<QuoteResponse>>(`${environment.apiUrl}/quotes/${id}`);
  }

  public createQuote(data: QuoteRequest): Observable<ApiResponse<CreatedQuoteResponse>> {
    return this.http.post<ApiResponse<CreatedQuoteResponse>>(`${environment.apiUrl}/quotes`, data);
  }

  // prettier-ignore
  public updateQuoteById(id: number, data: QuoteRequest): Observable<ApiResponse<CreatedQuoteResponse>> {
    return this.http.put<ApiResponse<CreatedQuoteResponse>>(
      `${environment.apiUrl}/quotes/${id}`,
      data,
    );
  }

  public updateQuoteStatus(id: number, quoteStatus: string): Observable<void> {
    return this.http.patch<void>(`${environment.apiUrl}/quotes/${id}/status`, { quoteStatus });
  }

  // Helper method that creates param
  private addParam(param: HttpParams, key: string, value: any): HttpParams {
    if (value === undefined) return param;
    return param.set(key, value.toString());
  }
}
