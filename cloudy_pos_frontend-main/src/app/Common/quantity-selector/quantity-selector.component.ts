import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-quantity-selector',
  templateUrl: './quantity-selector.component.html',
  styleUrls: ['./quantity-selector.component.css'],
  standalone: true,
})
export class QuantitySelectorComponent {
  @Input() count: number = 1; // Default to 1 instead of 0
  @Input() max: number = Infinity; // Maximum quantity (e.g., stockUnit)
  @Output() countChange = new EventEmitter<number>();

  increment() {
    if (this.count < this.max) {
      this.count++;
      this.countChange.emit(this.count);
    }
  }

  decrement() {
    if (this.count > 1) {
      // Minimum quantity should be 1
      this.count--;
      this.countChange.emit(this.count);
    }
  }
}
