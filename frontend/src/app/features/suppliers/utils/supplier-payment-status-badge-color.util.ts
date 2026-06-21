export function supplierPaymentStatusBadgeColor(status: string): string {
  switch (status) {
    case 'PENDING':
      return 'bg-warning-light text-warning-dark';
    case 'PAID':
      return 'bg-success-light text-success-dark';
    case 'PARTIALLY_PAID':
      return 'bg-info-light text-info-dark';
    case 'CANCELED':
      return 'bg-danger-light text-danger-dark';
    default:
      return 'bg-slate-200 text-slate-700';
  }
}
