export interface Supplier {
  id: number;
  companyName: string;
  email: string | null;
  isActive?: boolean;
  totalAmount?: number;
  remainingAmount?: number;
}
