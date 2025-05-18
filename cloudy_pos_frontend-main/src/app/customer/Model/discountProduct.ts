export interface DiscountProduct {
  id: number;
  productName: string;
  productDesc?: string;
  originalPrice: number;
  currency: string;
  discounts: {
    discountValue: number;
    isPercentage: boolean;
    startDate: string;
    endDate: string;
  }[];
  discountedPrice: number;
  imageUrls: {
    imgUrl: string;
    priority: number;
    main: boolean;
  }[];
  barcode: string;
  stockQuantity: number;
}
