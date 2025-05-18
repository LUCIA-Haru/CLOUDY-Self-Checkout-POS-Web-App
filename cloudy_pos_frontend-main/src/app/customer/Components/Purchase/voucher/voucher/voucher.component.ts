import {
  Component,
  Input,
  OnInit,
  AfterViewInit,
  ElementRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { CheckoutData } from 'app/customer/Model/checkOut';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { PdfService } from 'app/Service/pdf/pdf.service';
import { ToastService } from 'app/Service/toast/toast.service';
import { AnimationService } from 'app/Common/services/animation.service';

@Component({
  selector: 'app-voucher',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './voucher.component.html',
  styleUrls: ['./voucher.component.css'],
})
export class VoucherComponent implements OnInit, AfterViewInit {
  @Input() checkoutData!: CheckoutData;
  pdfUrl: SafeResourceUrl | null = null;
  pdfDownloadUrl: string | null = null;
  errorMessage: string | null = null;

  constructor(
    private sanitizer: DomSanitizer,
    private pdfService: PdfService,
    private toastService: ToastService,
    private animationService: AnimationService,
    private el: ElementRef,
  ) {}

  ngOnInit(): void {
    console.log(
      'VoucherComponent initialized, checkoutData:',
      this.checkoutData,
    );
    this.generateVoucher();
  }

  ngAfterViewInit(): void {
    this.applyAnimations();
  }

  generateVoucher(): void {
    if (this.checkoutData) {
      console.log('Generating voucher with checkoutData:', this.checkoutData);
      this.pdfService.generateVoucher(this.checkoutData).subscribe({
        next: (response) => {
          console.log('Generate voucher response:', response);
          if (response.status === 'success') {
            const filename = response.data.split('/').pop() || '';
            this.pdfService.getPdfUrl(filename).subscribe(
              (blob: Blob) => {
                const url = window.URL.createObjectURL(blob);
                this.pdfUrl =
                  this.sanitizer.bypassSecurityTrustResourceUrl(url);
                this.pdfDownloadUrl = url;
                console.log('PDF URL set:', this.pdfUrl);
                this.applySuccessAnimations();
              },
              (error) => {
                this.errorMessage = 'Error fetching PDF. Please try again.';
                console.error('Error fetching PDF:', error);
                this.toastService.showError('Failed to fetch PDF.');
              },
            );
          } else {
            this.errorMessage =
              'Failed to generate voucher: ' + response.message;
            console.error('Failed to generate PDF:', response.message);
            this.toastService.showError('Failed to generate PDF.');
          }
        },
        error: (error) => {
          this.errorMessage = 'Error generating voucher. Please try again.';
          console.error('Error generating PDF:', error);
          this.toastService.showError('Error generating PDF.');
        },
      });
    } else {
      this.errorMessage = 'Checkout data is missing.';
      console.error('CheckoutData is missing');
      this.toastService.showError('Checkout data is missing.');
    }
  }

  downloadVoucher(): void {
    if (this.pdfDownloadUrl) {
      console.log('Downloading voucher');
      const a = document.createElement('a');
      a.href = this.pdfDownloadUrl;
      a.download = 'voucher.pdf';
      a.click();
      this.animationService.pulse('.download-btn', {
        duration: 0.5,
        scale: 1.1,
        repeat: 1,
      });
    } else {
      console.error('Cannot download: PDF URL is missing');
      this.toastService.showError('Cannot download: PDF URL is missing.');
    }
  }

  shareVoucher(): void {
    if (this.pdfDownloadUrl) {
      // Placeholder for sharing logic (e.g., Web Share API)
      if (navigator.share) {
        navigator
          .share({
            title: 'My Purchase Voucher',
            text: 'Check out my purchase voucher!',
            url: this.pdfDownloadUrl,
          })
          .then(() => {
            this.toastService.showSuccess('Voucher shared successfully!');
            this.animationService.pulse('.share-btn', {
              duration: 0.5,
              scale: 1.1,
              repeat: 1,
            });
          })
          .catch((error) => {
            console.error('Error sharing voucher:', error);
            this.toastService.showError('Failed to share voucher.');
          });
      } else {
        // Fallback: Copy URL to clipboard
        navigator.clipboard.writeText(this.pdfDownloadUrl).then(() => {
          this.toastService.showSuccess('Voucher URL copied to clipboard!');
          this.animationService.pulse('.share-btn', {
            duration: 0.5,
            scale: 1.1,
            repeat: 1,
          });
        });
      }
    } else {
      this.toastService.showError('Cannot share: PDF URL is missing.');
    }
  }

  retryGenerateVoucher(): void {
    this.errorMessage = null;
    this.generateVoucher();
  }

  private applyAnimations(): void {
    // Animate header
    this.animationService.fadeIn('.voucher-header', 1);

    // Animate voucher card on scroll
    this.animationService.scrollZoomIn('.voucher-card', {
      duration: 1,
      scaleFrom: 0.9,
      start: 'top 85%',
    });

    // Animate buttons with stagger
    this.animationService.staggerScrollAppear('.action-buttons button', {
      duration: 0.8,
      y: 20,
      stagger: 0.2,
      start: 'top 85%',
    });

    // Animate loading/error states if present
    if (this.errorMessage) {
      this.animationService.bounceIn('.error-state', 1);
    } else if (!this.pdfUrl) {
      this.animationService.pulse('.spinner', {
        duration: 1,
        scale: 1.2,
        repeat: 3,
      });
    }
  }

  private applySuccessAnimations(): void {
    // Animate voucher card when PDF loads
    this.animationService.bounceIn('.voucher-card', 0.8);

    // Pulse buttons
    this.animationService.pulse('.download-btn', {
      duration: 0.5,
      scale: 1.1,
      repeat: 2,
    });
    this.animationService.pulse('.share-btn', {
      duration: 0.5,
      scale: 1.1,
      repeat: 2,
    });
  }
}
