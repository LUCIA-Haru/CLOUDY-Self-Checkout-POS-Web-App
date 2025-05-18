import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageUploadDialogComponentComponent } from './image-upload-dialog-component.component';

describe('ImageUploadDialogComponentComponent', () => {
  let component: ImageUploadDialogComponentComponent;
  let fixture: ComponentFixture<ImageUploadDialogComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImageUploadDialogComponentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImageUploadDialogComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
