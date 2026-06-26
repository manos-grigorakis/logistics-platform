export interface CompanyProfileBaseRequest {
  name: string;
  vatPercentage: number;
  representativeTitle: string;
  representative: string;
  street: string;
  streetNumber: string;
  postalCode: string;
  region: string;
  country: string;
  brandPrimaryColor?: string | null;
  brandSecondaryColor?: string | null;
  logoFile?: File | null;
  websiteUrl?: string | null;
  slogan?: string | null;
  phones: string[];
  email: string;
}
