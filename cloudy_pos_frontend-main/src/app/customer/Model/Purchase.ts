export interface PurchaseHistory {
  purchaseId: number;
  guid: string;
  userId: number;
  username: string;
  taxAmount: number;
  originalAmount: number;
  totalAmount: number;
  discountAmount: number;
  couponDiscount: number;
  finalAmount: number;
  status: string;
  couponId: number | null;
  createdOn: string;
  currency: string | null;
  items: PurchaseItem[];
  transaction: Transaction | null;
  voucherPdfUrl: string | null;
}

export interface PurchaseItem {
  purchaseItemId: number;
  guid: string;
  purchaseId: number;
  productId: number;
  quantity: number;
  price: number;
  subTotal: number;
}

export interface Transaction {
  transactionId: number;
  guid: string;
  purchaseId: number;
  paymentId: string;
  amount: number;
  status: string;
  paymentMethod: string;
  transactionDate: string;
  payerId: string | null;
  createdOn: string;
  failureReason: string | null;
}
