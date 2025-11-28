export interface UserRequest {
  firstName?: string;
  lastName?: string | null;
  email: string;
  phone: string | null;
  roleId: number;
}
