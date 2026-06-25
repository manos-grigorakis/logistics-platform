import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../shared/models/api-response.interface';
import { CompanyProfileResponse } from '../model/company-profile-response.interface';
import { environment } from '../../../../environments/environment';
import { CompanyProfileCreateRequest } from '../model/company-profile-create-request.interface';
import { CompanyProfileBaseRequest } from '../model/company-profile-base-request.interface';
import { CompanyProfileUpdateRequest } from '../model/company-profile-update-request.interface';

@Injectable({
  providedIn: 'root',
})
export class CompanyProfileService {
  private readonly http = inject(HttpClient);

  public fetchCompanyProfile(): Observable<ApiResponse<CompanyProfileResponse>> {
    return this.http.get<ApiResponse<CompanyProfileResponse>>(
      `${environment.apiUrl}/company-profile`,
    );
  }

  public createCompanyProfile(
    request: CompanyProfileCreateRequest,
  ): Observable<ApiResponse<CompanyProfileResponse>> {
    const form = new FormData();
    form.append('tin', request.tin);
    this.buildFormData(form, request);

    return this.http.post<ApiResponse<CompanyProfileResponse>>(
      `${environment.apiUrl}/company-profile`,
      form,
    );
  }

  public updateCompanyProfile(
    request: CompanyProfileUpdateRequest,
  ): Observable<ApiResponse<CompanyProfileResponse>> {
    const form = new FormData();
    this.buildFormData(form, request);

    return this.http.put<ApiResponse<CompanyProfileResponse>>(
      `${environment.apiUrl}/company-profile`,
      form,
    );
  }

  private buildFormData(form: FormData, request: CompanyProfileBaseRequest): void {
    form.append('name', request.name);
    form.append('vatPercentage', request.vatPercentage.toString());
    form.append('representativeTitle', request.representativeTitle);
    form.append('representative', request.representative);
    form.append('street', request.street);
    form.append('streetNumber', request.streetNumber);
    form.append('postalCode', request.postalCode);
    form.append('region', request.region);
    form.append('country', request.country);
    form.append('email', request.email);
    request.phones.forEach((phone: string) => form.append('phones', phone));

    // Optional
    if (request.brandPrimaryColor != null)
      form.append('brandPrimaryColor', request.brandPrimaryColor);
    if (request.brandSecondaryColor != null)
      form.append('brandSecondaryColor', request.brandSecondaryColor);
    if (request.logoFile != null) form.append('logoFile', request.logoFile);
    if (request.websiteUrl != null) form.append('websiteUrl', request.websiteUrl);
    if (request.slogan != null) form.append('slogan', request.slogan);
  }
}
