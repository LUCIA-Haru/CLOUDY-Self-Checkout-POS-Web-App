import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffDataComponent } from './staff-data.component';

describe('StaffDataComponent', () => {
  let component: StaffDataComponent;
  let fixture: ComponentFixture<StaffDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StaffDataComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StaffDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
