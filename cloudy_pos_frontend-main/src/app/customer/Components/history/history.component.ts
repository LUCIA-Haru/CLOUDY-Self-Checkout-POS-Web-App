import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  Component,
  OnInit,
  QueryList,
  ViewChildren,
  ElementRef,
  ChangeDetectorRef,
  AfterViewChecked,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AnimationService } from 'app/Common/services/animation.service';
import { ApiService } from 'app/customer/apiService/api.service';
import { PurchaseHistory } from 'app/customer/Model/Purchase';
import { ToastService } from 'app/Service/toast/toast.service';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css'],
})
export class HistoryComponent
  implements OnInit, AfterViewInit, AfterViewChecked
{
  purchases: PurchaseHistory[] = [];
  loading: boolean = true;
  private animationsApplied: boolean = false;

  @ViewChildren('historyCard') historyCards!: QueryList<ElementRef>;
  @ViewChildren('purchaseDetails') purchaseDetails!: QueryList<ElementRef>;
  @ViewChildren('voucherButton') voucherButtons!: QueryList<ElementRef>;

  constructor(
    private animationService: AnimationService,
    private toastr: ToastService,
    private apiService: ApiService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadPurchaseHistory();
  }

  ngAfterViewInit(): void {
    // Initial animation attempt
    this.applyAnimations();
    this.historyCards.changes.subscribe(() => {
      console.log('History cards changed:', this.historyCards.length);
      this.applyAnimations();
    });
  }

  ngAfterViewChecked(): void {
    // Reapply animations if purchases are loaded and animations haven't been applied
    if (this.purchases.length > 0 && !this.animationsApplied && !this.loading) {
      console.log('Applying animations after view checked');
      this.applyAnimations();
      this.cdr.detectChanges();
    }
  }

  applyAnimations(): void {
    if (this.historyCards.length === 0) {
      console.warn('No history cards found for animation');
      return;
    }

    // Animate cards
    this.historyCards.forEach((card, index) => {
      const cardElement = card.nativeElement;
      console.log(`Animating card ${index}:`, cardElement);
      if (index % 3 === 0) {
        this.animationService.scrubSlideFromLeft(cardElement, {
          distance: 200,
          start: 'top 90%',
          end: 'bottom 10%',
        });
      } else if (index % 3 === 1) {
        this.animationService.scrubSlideFromBottom(cardElement, {
          distance: 200,
          start: 'top 90%',
          end: 'bottom 10%',
        });
      } else {
        this.animationService.scrubSlideFromRight(cardElement, {
          distance: 200,
          start: 'top 90%',
          end: 'bottom 10%',
        });
      }
      this.animationService.parallaxMove(cardElement, {
        distance: 50,
        start: 'top bottom',
        end: 'bottom top',
      });
      this.animationService.glowEffect(cardElement, {
        duration: 1.5,
        start: 'top 90%',
        glowColor: '#ffffff33',
      });
    });

    // Animate purchase details
    this.purchaseDetails.forEach((detail, index) => {
      const detailElements = detail.nativeElement.querySelectorAll('p');
      console.log(
        `Animating details ${index}:`,
        detailElements.length,
        'elements',
      );
      this.animationService.staggerScrollAppear(detailElements, {
        duration: 1,
        y: 30,
        stagger: 0.15,
        start: 'top 85%',
      });
    });

    // Animate buttons
    this.voucherButtons.forEach((button, index) => {
      const buttonElement = button.nativeElement;
      console.log(`Animating button ${index}:`, buttonElement);
      this.animationService.pulse(buttonElement, {
        duration: 0.8,
        scale: 1.05,
        repeat: 3,
      });
      this.animationService.flipIn(buttonElement, {
        duration: 1,
        direction: 'x',
      });
    });

    this.animationsApplied = true;
  }

  loadPurchaseHistory(): void {
    this.loading = true;
    this.animationsApplied = false; // Reset animations on new data load
    this.apiService.fetchPurchaseHistory().subscribe({
      next: (response: { data: any }) => {
        console.log('Fetched Purchase History:', response.data);
        this.purchases = response.data;
        this.loading = false;
        this.cdr.detectChanges(); // Trigger change detection
      },
      error: (error: any) => {
        console.error('API Error:', error);
        this.toastr.showError('Error Fetching History');
        this.loading = false;
      },
    });
  }

  downloadVoucher(voucherUrl: string | null): void {
    if (!voucherUrl) {
      this.toastr.showError('No voucher available for download');
      return;
    }
    const filename = voucherUrl.substring(voucherUrl.lastIndexOf('/') + 1);
    console.log(filename);
    this.apiService.downloadPdf(filename).subscribe({
      next: (blob: Blob) => {
        // Create a URL for the blob
        const url = window.URL.createObjectURL(blob);

        // Create a temporary anchor element to trigger the download
        const link = document.createElement('a');
        link.href = url;
        link.download = filename; // Use filename from URL
        document.body.appendChild(link);
        link.click();

        // Clean up
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);

        this.toastr.showSuccess('Voucher downloaded successfully');
      },
      error: (error: any) => {
        console.error('Download Error:', error);
        this.toastr.showError('Failed to download voucher');
      },
    });
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'COMPLETED':
        return 'status-completed';
      case 'PENDING':
        return 'status-pending';
      case 'CANCELLED':
        return 'status-cancelled';
      default:
        return 'status-default';
    }
  }
}
