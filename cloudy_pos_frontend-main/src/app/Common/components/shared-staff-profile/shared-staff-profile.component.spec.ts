import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedStaffProfileComponent } from './shared-staff-profile.component';

describe('SharedStaffProfileComponent', () => {
  let component: SharedStaffProfileComponent;
  let fixture: ComponentFixture<SharedStaffProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedStaffProfileComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SharedStaffProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
