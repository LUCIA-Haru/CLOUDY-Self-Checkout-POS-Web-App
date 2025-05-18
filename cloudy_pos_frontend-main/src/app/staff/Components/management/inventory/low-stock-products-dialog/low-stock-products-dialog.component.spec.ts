import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LowStockProductsDialogComponent } from './low-stock-products-dialog.component';

describe('LowStockProductsDialogComponent', () => {
  let component: LowStockProductsDialogComponent;
  let fixture: ComponentFixture<LowStockProductsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LowStockProductsDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LowStockProductsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
