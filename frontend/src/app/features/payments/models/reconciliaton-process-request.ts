export interface ReconciliationProcessRequest {
  customerId: number;
  invoiceFile: File;
  bankStatementFile: File;
}
