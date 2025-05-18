// product.interface.ts
export interface Product {
  productId: number;
  productGuid: string;
  productName: string;
  categoryId: number;
  category: string;
  transactionId: number;
  productDesc: string;
  brandId: number;
  brand: string;
  price: number;
  currency: string;
  stockUnit: number;
  expDate: string;
  manuDate: string;
  rating: number;
  imgUrls: ProductImage[] | null;
  createdOn: string;
  createdBy: string;
  updatedOn: string | null;
  updatedBy: string | null;
  barcode: string;
  hasDiscount: boolean;
  discount: DiscountAll | null;
}

// product-image.interface.ts
export interface ProductImage {
  imgUrl: string;
  priority: number;
  main: boolean;
}

// discount.interface.ts
export interface DiscountAll {
  discountId: number;
  guid: string;
  discountValue: number;
  isPercentage: boolean;
  startDate: string;
  endDate: string;
  productId: number | null;
  categoryId: number | null;
  productName?: string;
  categoryName?: string;
}
