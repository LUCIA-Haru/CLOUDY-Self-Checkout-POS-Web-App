import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlldashboardComponent } from './alldashboard.component';

describe('AlldashboardComponent', () => {
  let component: AlldashboardComponent;
  let fixture: ComponentFixture<AlldashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlldashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlldashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
