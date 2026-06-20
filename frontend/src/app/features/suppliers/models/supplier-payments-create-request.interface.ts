export interface SupplierPaymentsCreateRequest {
  title: string;
  description?: string;
  totalAmount: number;
  paidAmount?: number;
  type: string;
  invoiceFile?: File;
  receiptFile?: File;
  supplierId: number;
}
