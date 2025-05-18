import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StaticPartsComponent } from './static-parts.component';

describe('StaticPartsComponent', () => {
  let component: StaticPartsComponent;
  let fixture: ComponentFixture<StaticPartsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StaticPartsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StaticPartsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
