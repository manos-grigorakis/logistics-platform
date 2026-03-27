export interface ReconciliationProcessRequest {
  customerId: number;
  invoiceFile: File;
  bankStatement: File;
}
