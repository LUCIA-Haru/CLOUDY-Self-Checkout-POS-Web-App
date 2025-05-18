import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtpVerficiationComponent } from './otp-verficiation.component';

describe('OtpVerficiationComponent', () => {
  let component: OtpVerficiationComponent;
  let fixture: ComponentFixture<OtpVerficiationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OtpVerficiationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OtpVerficiationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
