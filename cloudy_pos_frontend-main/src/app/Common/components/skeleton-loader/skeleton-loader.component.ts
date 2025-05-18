import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-skeleton-loader',
  imports: [CommonModule],
  templateUrl: './skeleton-loader.component.html',
  styleUrl: './skeleton-loader.component.css',
})
export class SkeletonLoaderComponent {
  @Input() type: 'table' | 'card' | 'list' = 'table'; // Define different skeleton types
  @Input() rows: number = 5; // Number of rows for table/list
  @Input() columns: number = 5; // Number of columns for table
  @Input() width: string = '100%'; // Custom width
  @Input() height: string = 'auto'; // Custom height

  get skeletonRows(): number[] {
    return Array(this.rows).fill(0);
  }

  get skeletonColumns(): number[] {
    return Array(this.columns).fill(0);
  }
}
