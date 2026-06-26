export interface BasicDetailsReview {
  tin: string;
  name: string;
  vatPercentage: number;
  representativeTitle: string;
  representative: string;
}

export interface AddressReview {
  street: string;
  streetNumber: string;
  postalCode: string;
  region: string;
  country: string;
}

export interface ContactReview {
  email: string;
  phones: string[];
}

export interface BrandingReview {
  brandPrimaryColor: string;
  brandSecondaryColor: string;
  websiteUrl: string;
  slogan: string;
  logoFile: File | null;
}
