export interface CreateUserRequest {
  firstName?: string;
  lastName?: string | null;
  email: string;
  phone: string | null;
  roleId: number;
}
