export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string | null;
  roleId: number;
  status: string;
  enabled: boolean;
  createdAt: string;
  updatedAt: string | null;
}
