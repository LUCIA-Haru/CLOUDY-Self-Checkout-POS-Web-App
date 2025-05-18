import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { BrowserMultiFormatReader, Result } from '@zxing/library';
import { ToastService } from 'app/Service/toast/toast.service';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-barcode-scanner',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './barcode-scanner.component.html',
  styleUrls: ['./barcode-scanner.component.css'],
})
export class BarcodeScannerComponent implements OnInit, OnDestroy {
  scannedBarcode: string = ''; // Store scanned barcode result
  manualBarcode: string = '';
  private codeReader = new BrowserMultiFormatReader();
  private videoElement!: HTMLVideoElement;
  private canvasElement!: HTMLCanvasElement;
  private canvasContext!: CanvasRenderingContext2D | null;
  private stream!: MediaStream;

  constructor(
    private toastService: ToastService,
    private http: HttpClient,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.startCamera();
  }

  ngOnDestroy(): void {
    this.stopCamera();
  }

  // Start the camera
  startCamera(): void {
    navigator.mediaDevices
      .getUserMedia({ video: { facingMode: 'environment' } })
      .then((stream) => {
        this.stream = stream;
        this.videoElement = document.createElement('video');
        this.videoElement.srcObject = stream;
        this.videoElement.setAttribute('playsinline', 'true'); // iOS support
        this.videoElement.autoplay = true;

        // Ensure video element scales correctly
        this.videoElement.style.width = '100%';
        this.videoElement.style.height = '100%';
        this.videoElement.style.objectFit = 'cover'; // Match CSS

        // Append video element to container
        const videoContainer = document.getElementById('video-container');
        if (videoContainer) {
          videoContainer.innerHTML = ''; // Clear previous content
          videoContainer.appendChild(this.videoElement);
        }

        // Create canvas for capturing images
        this.canvasElement = document.createElement('canvas');
        this.canvasContext = this.canvasElement.getContext('2d');

        // Wait for video metadata to load to set canvas dimensions
        this.videoElement.onloadedmetadata = () => {
          this.canvasElement.width = this.videoElement.videoWidth;
          this.canvasElement.height = this.videoElement.videoHeight;
        };
      })
      .catch((error) => {
        this.toastService.showError('Error accessing camera.');
        console.error('Error accessing camera:', error);
      });
  }

  // Capture a snapshot from the video feed
  captureSnapshot(): void {
    if (!this.videoElement || !this.canvasElement || !this.canvasContext) {
      this.toastService.showError('Camera or canvas is not initialized.');
      return;
    }

    // Set canvas size to match video dimensions
    this.canvasElement.width = this.videoElement.videoWidth;
    this.canvasElement.height = this.videoElement.videoHeight;

    // Draw the current video frame onto the canvas
    this.canvasContext.drawImage(
      this.videoElement,
      0,
      0,
      this.canvasElement.width,
      this.canvasElement.height,
    );

    // Convert canvas to an image element
    const image = new Image();
    image.src = this.canvasElement.toDataURL('image/png');

    // Wait for the image to load
    image.onload = () => {
      this.decodeBarcodeFromImage(image);
    };
  }

  // Decode barcode from the captured image
  decodeBarcodeFromImage(image: HTMLImageElement): void {
    this.codeReader
      .decodeFromImage(image)
      .then((result: Result) => {
        let scannedResult = result.getText().trim();

        // Check if barcode is 12 digits (UPC-A) and force conversion to 13 digits (EAN-13)
        if (scannedResult.length === 12) {
          scannedResult = '0' + scannedResult; // Add leading zero
        }

        this.scannedBarcode = scannedResult; // Store the fixed barcode
        this.toastService.showSuccess('Barcode scanned successfully!');
        this.stopCamera(); // Stop the camera after successful scan
        console.log('Scanned Barcode:', this.scannedBarcode);
      })
      .catch((error: any) => {
        console.error('No barcode detected:', error);
        this.scannedBarcode = 'No barcode detected';
        this.toastService.showError('No barcode detected. Please try again.');
      });
  }

  // Stop the camera when component is destroyed
  stopCamera(): void {
    if (this.stream) {
      this.stream.getTracks().forEach((track) => track.stop());
    }
  }

  // After barcode -> Show details of the product
  searchProduct(barcode: string): void {
    barcode = barcode.trim();
    const apiUrl = `/product/search/${encodeURIComponent(barcode)}`;
    console.log('API URL:', apiUrl);

    this.http.get(apiUrl).subscribe({
      next: (product: any) => {
        if (product) {
          this.router.navigate([`/product/search/${barcode}`], {
            state: { productDetails: product },
          });
        } else {
          this.toastService.showWarning('Product not found.');
        }
      },
      error: () => {
        this.toastService.showError('Error fetching product details.');
      },
    });
  }

  // Handle manual barcode search
  searchManualBarcode(): void {
    if (!this.manualBarcode || this.manualBarcode.trim() === '') {
      this.toastService.showError('Please enter a valid barcode.');
      return;
    }

    let barcode = this.manualBarcode.trim();

    // Check if barcode is 12 digits (UPC-A) and force conversion to 13 digits (EAN-13)
    if (barcode.length === 12 && /^\d{12}$/.test(barcode)) {
      barcode = '0' + barcode; // Add leading zero
    }

    // Validate barcode (ensure it contains only digits and is of valid length)
    if (!/^\d{13}$/.test(barcode)) {
      this.toastService.showError(
        'Invalid barcode format. Please enter a valid 12 or 13-digit barcode.',
      );
      return;
    }

    this.scannedBarcode = barcode; // Update scannedBarcode to display in UI
    this.searchProduct(barcode);
  }
}
