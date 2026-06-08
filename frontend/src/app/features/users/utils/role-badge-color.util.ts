export function roleBadgeColor(roleName: string): string {
  switch (roleName) {
    case 'ADMIN':
      return 'bg-danger-light text-danger-dark';
    case 'MANAGER':
      return 'bg-warning-light text-warning-dark';
    case 'EMPLOYEE':
      return 'bg-info-light text-info-dark';
    default:
      return 'bg-secondary-light text-secondary-dark';
  }
}
