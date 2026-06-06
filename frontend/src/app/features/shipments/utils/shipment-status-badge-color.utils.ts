export function shipmentStatusBadgeColor(status: string): string {
  switch (status) {
    case 'pending':
      return 'bg-primary-100 text-primary-800';
    case 'dispatched':
      return 'bg-warning-light text-warning-dark';
    case 'delivered':
      return 'bg-success-light text-success-dark';
    default:
      return 'bg-slate-200 text-slate-700';
  }
}
