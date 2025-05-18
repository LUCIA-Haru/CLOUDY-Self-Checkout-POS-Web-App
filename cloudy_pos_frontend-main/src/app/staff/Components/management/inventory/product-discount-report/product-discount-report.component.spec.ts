import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductDiscountReportComponent } from './product-discount-report.component';

describe('ProductDiscountReportComponent', () => {
  let component: ProductDiscountReportComponent;
  let fixture: ComponentFixture<ProductDiscountReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductDiscountReportComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductDiscountReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
