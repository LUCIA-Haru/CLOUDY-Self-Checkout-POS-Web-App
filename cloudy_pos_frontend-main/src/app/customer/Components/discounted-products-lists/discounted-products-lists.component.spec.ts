import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DiscountedProductsListsComponent } from './discounted-products-lists.component';

describe('DiscountedProductsListsComponent', () => {
  let component: DiscountedProductsListsComponent;
  let fixture: ComponentFixture<DiscountedProductsListsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DiscountedProductsListsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DiscountedProductsListsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
