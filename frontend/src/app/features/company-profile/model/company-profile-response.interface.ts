import { CompanyProfileAddressSummary } from './summary/company-profile-address-summary.interface';
import { CompanyProfileBrandingSummary } from './summary/company-profile-branding-summary.interface';

export interface CompanyProfileResponse {
  id: number;
  name: string;
  tin: string;
  slogan: string | null;
  logoUrl: string | null;
  vatPercentage: number;
  representativeTitle: string;
  representative: string;
  email: string;
  phones: string[];
  websiteUrl: string | null;
  address: CompanyProfileAddressSummary;
  branding: CompanyProfileBrandingSummary;
  createdAt: string;
  updatedAt: string;
}
