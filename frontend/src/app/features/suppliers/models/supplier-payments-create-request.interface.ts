export interface SupplierPaymentsCreateRequest {
  title: string;
  description: string | null;
  totalAmount: number;
  paidAmount: number | null;
  type: string;
  invoiceFile: File | null;
  receiptFile: File | null;
  supplierId: number;
}
