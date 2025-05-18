import { Product } from 'app/customer/Model/productAll';
import { ProductDTO } from '../service/discounts/discounts.service';

export interface SupplierTransactionRequest {
  supplierId: number;
  brandId: number;
  categoryId: number;
  productId: number;
  quantity: number;
  transactionDate: string;
  manuDate: string;
  expDate: string;
}

export interface SupplierTransactions {
  transactionId: number;
  supplierId: number;
  brandId: number;
  categoryId: number;
  quantity: number;
  transactionDate: string;
  createdBy: string;
  product: Product[];
}
