export interface Category {
  categoryId: number;
  guid: string;
  categoryName: string;
  categoryType: string;
  categoryDesc: string | null;
  aisle: number;
  createdOn: string;
  createdBy: string;
  updatedOn: string | null;
  updatedBy: string | null;
}

export interface Brand {
  id: number;
  guid: string;
  name: string;
  photo: string | null;
  isActive: boolean;
  createdOn: string;
  createdBy: string;
  updatedOn: string | null;
  updatedBy: string | null;
}
