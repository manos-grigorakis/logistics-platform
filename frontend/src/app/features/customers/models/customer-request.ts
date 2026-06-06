export interface CustomerRequest {
  tin: string;
  companyName: string;
  firstName: string;
  lastName: string;
  email: string | null;
  customerType: string;
  location?: string | null;
  phone?: string | null;
}
