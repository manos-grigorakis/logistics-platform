export function customerTypeBadgeColor(type: string): string {
  switch (type) {
    case 'COMPANY':
      return 'bg-primary-100 text-primary-800';
    case 'INDIVIDUAL':
      return 'bg-success-light text-success-dark';
    default:
      return 'bg-secondary-100 text-secondary-800';
  }
}
