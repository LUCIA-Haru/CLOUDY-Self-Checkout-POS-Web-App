import { Discount } from './../discount';

export interface CartItems {
  img?: string;
  productId: number;
  productGuid: string;
  productName: string;
  barcode: string;
  brand: string;
  cartQuantity: number;
  categoryId: number;
  createdBy: string;
  createdOn: string;
  currency: string;
  discount?: Discount;
  expDate: string;
  hasDiscount: boolean;
  manuDate: string;
  price: number;
  prodImgURL: string | null;
  productDesc: string;
  rating: number;
  stockUnit: number;
  updatedBy: string | null;
  updatedOn: string | null;
}
