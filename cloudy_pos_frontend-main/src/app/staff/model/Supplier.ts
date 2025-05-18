export interface Supplier {
  id: number;
  guid?: string;
  name: string;
  contactEmail: string;
  contactPhone: string;
  isMainSupplier: boolean;
  contractDurationInMonths: number;
  isActive: boolean;
  createdOn?: string;
  createdBy?: string;
  updatedOn?: string | null;
  updatedBy?: string | null;
}
