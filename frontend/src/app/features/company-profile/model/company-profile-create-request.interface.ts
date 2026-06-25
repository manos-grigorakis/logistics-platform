import { CompanyProfileBaseRequest } from './company-profile-base-request.interface';

export interface CompanyProfileCreateRequest extends CompanyProfileBaseRequest {
  tin: string;
}
