import { ErrorResponse } from './error-response.interface';

export interface ApiResponse<T> {
  transactionId: string;
  data: T;
  timestamp: string;
  error: ErrorResponse;
}
