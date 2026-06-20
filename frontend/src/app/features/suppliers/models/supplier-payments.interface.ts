import { SupplierSummary } from './supplier-summary.interface';

export interface SupplierPayment {
  id: number;
  number: string;
  title: string;
  description?: string;
  totalAmount: number;
  paidAmount: number;
  unpaidAmount: number;
  status: string;
  type: string;
  invoiceUrl?: string;
  receiptUrl?: string;
  supplier: SupplierSummary;
  createdAt: string;
  updatedAt?: string;
}
