export interface CartItem {
  img?: string;
  barcode: string;
  name: string;
  price: number;
  discountedPrice: number;
  quantity: number;
}

export interface CheckoutData {
  cartItems: CartItem[];
  totalAmount: number; // After discounts applied to items
  originalAmount: number; // Before any discounts
  couponCode?: string | null;
  couponDiscount: number;
  loyaltyPointsUsed: number;
  loyaltyDiscount: number;
  finalAmount: number; // Grand total including tax
  taxAmount: number;
  username: string;
  purchaseId?: number;
  paymentId?: string; // Optional, set after initiation
  payerId?: string | null;
  pointsEarned?: number | null;
  discountAmount?: number; // Optional, total discount (coupon + loyalty)
  approvalURl?: string;
  transactionStatus?: string;
}
