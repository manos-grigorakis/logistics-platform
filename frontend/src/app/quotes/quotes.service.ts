import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from '../auth/services/auth.service';
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
  private authService: AuthService = inject(AuthService);
  private jwtToken?: string | null;

  public fetchQuotes(param: FetchQuotesParameters = {}): Observable<FetchQuotesResponse> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    let params = new HttpParams();
    params = this.addParam(params, 'page', param.page);
    params = this.addParam(params, 'size', param.size);
    params = this.addParam(params, 'sortBy', param.sortBy);
    params = this.addParam(params, 'sortDirection', param.sortDirection);
    params = this.addParam(params, 'number', param.number);
    params = this.addParam(params, 'companyName', param.companyName);
    params = this.addParam(params, 'quoteStatus', param.quoteStatus);

    return this.http.get<FetchQuotesResponse>(`${environment.apiUrl}/quotes`, {
      headers: headers,
      params: params,
    });
  }

  public fetchQuoteById(id: number): Observable<QuoteResponse> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    return this.http.get<QuoteResponse>(`${environment.apiUrl}/quotes/${id}`, { headers: headers });
  }

  public createQuote(data: QuoteRequest): Observable<CreatedQuoteResponse> {
    this.jwtToken = this.authService.getJwtToken();
    const headers = { Authorization: `Bearer ${this.jwtToken}` };

    return this.http.post<CreatedQuoteResponse>(`${environment.apiUrl}/quotes`, data, {
      headers: headers,
    });
  }

  // Helper method that creates param
  private addParam(param: HttpParams, key: string, value: any): HttpParams {
    if (value === undefined) return param;
    return param.set(key, value.toString());
  }
}
