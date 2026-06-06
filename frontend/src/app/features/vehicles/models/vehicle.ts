export interface Vehicle {
  id: number;
  brand: string;
  plate: string;
  type: 'truck' | 'trailer';
  createdAt: string;
  updatedAt: string;
}
