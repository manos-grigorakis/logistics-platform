export interface ErrorResponse {
  status: number;
  message: string;
  errorCode?: string;
  details?: unknown;
}
