import { Discount } from './../discount';

export interface CartItemDisplay {
  productName: string;
  price: number;
  cartQuantity: number;
  currency: string;
  hasDiscount: boolean;
  discount?: Discount;
  prodImgURL?: string | null;
  barcode: string; // Add barcode explicitly since it's used
  discountedPrice: number; // Change from string to number
}
