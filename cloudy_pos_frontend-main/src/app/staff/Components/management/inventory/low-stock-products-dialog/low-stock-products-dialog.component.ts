import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-low-stock-products-dialog',
  imports: [MatDialogModule, CommonModule, MatButtonModule],
  templateUrl: './low-stock-products-dialog.component.html',
  styleUrl: './low-stock-products-dialog.component.css',
})
export class LowStockProductsDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      lowStockProducts: { productId: number; productName: string }[];
    },
  ) {}
}
