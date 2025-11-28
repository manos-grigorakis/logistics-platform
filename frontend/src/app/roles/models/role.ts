export interface Role {
  id: number;
  name: string;
  description: string | null;
  editable: boolean;
  createdAt: string;
  updatedAt: string | null;
}
