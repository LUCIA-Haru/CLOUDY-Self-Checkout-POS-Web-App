import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaypalReturnComponent } from './paypal-return.component';

describe('PaypalReturnComponent', () => {
  let component: PaypalReturnComponent;
  let fixture: ComponentFixture<PaypalReturnComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaypalReturnComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaypalReturnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
