import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuideSectionComponent } from './guide-section.component';

describe('GuideSectionComponent', () => {
  let component: GuideSectionComponent;
  let fixture: ComponentFixture<GuideSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuideSectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GuideSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
