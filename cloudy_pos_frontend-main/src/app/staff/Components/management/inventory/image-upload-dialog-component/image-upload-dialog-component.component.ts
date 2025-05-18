import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { InventoryService } from 'app/staff/service/inventory/inventory.service';
import { ToastrService } from 'ngx-toastr';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-image-upload-dialog-component',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule],
  templateUrl: './image-upload-dialog-component.component.html',
  styleUrl: './image-upload-dialog-component.component.css',
})
export class ImageUploadDialogComponentComponent {
  files: File[] = [];
  priorities: number[] = [];
  imageDetails: { imgUrl: string; main: boolean; priority: number }[] = [];

  constructor(
    public dialogRef: MatDialogRef<ImageUploadDialogComponentComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      productId: number;
      imgUrls: string[];
      imageDetails: { imgUrl: string; main: boolean; priority: number }[];
    },
    private inventoryService: InventoryService,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef,
  ) {
    this.imageDetails =
      this.data.imageDetails && Array.isArray(this.data.imageDetails)
        ? this.data.imageDetails.filter(
            (img) => img.imgUrl && !img.imgUrl.includes('No Images'),
          )
        : [];
  }

  onFileChange(event: any) {
    this.files = Array.from(event.target.files);
    this.priorities = this.files.map((_, index) => index);
  }

  uploadImages() {
    if (this.files.length === 0) {
      this.toastr.warning('No files selected', 'Warning');
      return;
    }

    this.inventoryService
      .uploadProductImages(this.data.productId, this.files, this.priorities)
      .subscribe({
        next: (response: any) => {
          if (response.status === 'success') {
            this.toastr.success('Images uploaded successfully', 'Success');
            if (response.data && response.data.imageDTOs) {
              // Map imageDTOs to imageDetails format
              const newImages = response.data.imageDTOs.map(
                (
                  item: { imgUrl: string; main: boolean; priority: number },
                  index: number,
                ) => ({
                  imgUrl: item.imgUrl,
                  main: item.main, // Use the main value from the response
                  priority: item.priority, // Use the priority from the response
                }),
              );
              console.log('New images to add:', newImages);

              // Update imageDetails with new images
              this.imageDetails = [...this.imageDetails, ...newImages];
              console.log('Updated imageDetails:', this.imageDetails);
            }

            // Clear the file input
            this.files = [];
            this.priorities = [];

            // Trigger change detection to update the UI
            this.cdr.detectChanges();
            // Do not close the dialog, allowing more uploads
          } else {
            this.toastr.error(response.message, 'Error');
          }
        },
        error: () => this.toastr.error('Failed to upload images', 'Error'),
      });
  }

  deleteImage(imgUrl: string) {
    this.inventoryService
      .deleteProductImage(this.data.productId, imgUrl)
      .subscribe({
        next: (response: any) => {
          if (response.status === 'success') {
            this.toastr.success('Image deleted successfully', 'Success');
            this.imageDetails = this.imageDetails.filter(
              (img) => img.imgUrl !== imgUrl,
            );
          } else {
            this.toastr.error(response.message, 'Error');
          }
        },
        error: () => this.toastr.error('Failed to delete image', 'Error'),
      });
  }

  setMainImage(imgUrl: string) {
    this.inventoryService
      .updateMainImage(this.data.productId, imgUrl)
      .subscribe({
        next: (response: any) => {
          if (response.status === 'success') {
            this.toastr.success('Main image updated successfully', 'Success');
            this.imageDetails = this.imageDetails.map((img) => ({
              ...img,
              main: img.imgUrl === imgUrl,
            }));
            this.dialogRef.close(true);
          } else {
            this.toastr.error(response.message, 'Error');
          }
        },
        error: () => this.toastr.error('Failed to update main image', 'Error'),
      });
  }

  close() {
    this.dialogRef.close();
  }
  save() {
    this.dialogRef.close(true); // Close dialog and signal that changes should be applied
  }
}
