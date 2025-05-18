import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpiryProductReportComponent } from './expiry-product-report.component';

describe('ExpiryProductReportComponent', () => {
  let component: ExpiryProductReportComponent;
  let fixture: ComponentFixture<ExpiryProductReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpiryProductReportComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpiryProductReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
