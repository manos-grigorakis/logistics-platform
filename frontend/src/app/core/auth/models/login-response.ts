export interface LoginResponse {
  tokenType: string;
  token: string;
  user: { id: number; email: string; firstName: string; lastName: string; role: string };
}
