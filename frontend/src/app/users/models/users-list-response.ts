export interface UsersListResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  roleId: number;
  status: string;
  enable: boolean;
}
