export interface UserResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  roleId: number;
  status: string;
  enable: boolean;
  createdAt: string;
  updatedAt: string | null;
}
