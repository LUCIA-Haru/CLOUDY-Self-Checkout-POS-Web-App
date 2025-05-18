import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  constructor(private toastr: ToastrService) {}
  showSuccess(message: string, title: string = 'Success'): void {
    this.toastr.success(message, title, {
      positionClass: 'toast-top-right',
      closeButton: false,
      timeOut: 5000,
      progressBar: true,
      extendedTimeOut: 2000, // Stay visible for 2 more seconds after hover
      tapToDismiss: true, // Dismiss on click
    });
  }

  showError(message: string, title: string = 'Error'): void {
    this.toastr.error(message, title, {
      positionClass: 'toast-top-right',
      closeButton: true,
      timeOut: 5000,
      progressBar: true,
      extendedTimeOut: 2000, // Stay visible for 2 more seconds after hover
      // tapToDismiss: true, // Dismiss on click
    });
  }

  showWarning(message: string, title: string = 'Warning'): void {
    this.toastr.warning(message, title, {
      positionClass: 'toast-top-right',
      closeButton: true,
      timeOut: 5000,
      progressBar: true,
      extendedTimeOut: 2000, // Stay visible for 2 more seconds after hover
      // tapToDismiss: true, // Dismiss on click
    });
  }

  showInfo(message: string, title: string = 'Info'): void {
    this.toastr.info(message, title, {
      positionClass: 'toast-top-right',
      closeButton: true,
      timeOut: 5000,
      progressBar: true,
      extendedTimeOut: 2000, // Stay visible for 2 more seconds after hover
      // tapToDismiss: true, // Dismiss on click
    });
  }
}
