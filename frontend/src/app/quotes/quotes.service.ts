import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FetchQuotesResponse } from './models/fetch-quotes-response';
import { environment } from '../../environments/environment';
import { QuoteResponse } from './models/quote-response';
import { FetchQuotesParameters } from './models/fetch-quotes-parameters';
import { QuoteRequest } from './models/quote-request';
import { CreatedQuoteResponse } from './models/created-quote-response';

@Injectable({
  providedIn: 'root',
})
export class QuotesService {
  private http: HttpClient = inject(HttpClient);

  public fetchQuotes(param: FetchQuotesParameters = {}): Observable<FetchQuotesResponse> {
    let params = new HttpParams();
    params = this.addParam(params, 'page', param.page);
    params = this.addParam(params, 'size', param.size);
    params = this.addParam(params, 'sortBy', param.sortBy);
    params = this.addParam(params, 'sortDirection', param.sortDirection);
    params = this.addParam(params, 'number', param.number);
    params = this.addParam(params, 'companyName', param.companyName);
    params = this.addParam(params, 'quoteStatus', param.quoteStatus);

    return this.http.get<FetchQuotesResponse>(`${environment.apiUrl}/quotes`, {
      params: params,
    });
  }

  public fetchQuoteById(id: number): Observable<QuoteResponse> {
    return this.http.get<QuoteResponse>(`${environment.apiUrl}/quotes/${id}`);
  }

  public createQuote(data: QuoteRequest): Observable<CreatedQuoteResponse> {
    return this.http.post<CreatedQuoteResponse>(`${environment.apiUrl}/quotes`, data);
  }

  public updateQuoteById(id: number, data: QuoteRequest): Observable<CreatedQuoteResponse> {
    return this.http.put<CreatedQuoteResponse>(`${environment.apiUrl}/quotes/${id}`, data);
  }

  // Helper method that creates param
  private addParam(param: HttpParams, key: string, value: any): HttpParams {
    if (value === undefined) return param;
    return param.set(key, value.toString());
  }
}
