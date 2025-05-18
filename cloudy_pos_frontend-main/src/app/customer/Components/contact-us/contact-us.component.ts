import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import emailjs, { EmailJSResponseStatus } from '@emailjs/browser';
import { AnimationService } from 'app/Common/services/animation.service';
import { ToastService } from 'app/Service/toast/toast.service';
@Component({
  selector: 'app-contact-us',
  imports: [FormsModule, CommonModule],
  templateUrl: './contact-us.component.html',
  styleUrl: './contact-us.component.css',
})
export class ContactUsComponent implements OnInit {
  contactName: string = '';
  contactEmail: string = '';
  contactDesc: string = '';
  contactMessage: string = '';
  messageClass: string = '';

  constructor(
    private animation: AnimationService,
    private toastr: ToastService,
  ) {}

  ngOnInit(): void {
    // Initialize EmailJS with your public key
    emailjs.init('Mg6a2JkR_FLKA-ouK');
  }

  sendEmail(e: Event): void {
    e.preventDefault();

    // Validate form inputs
    if (!this.contactName || !this.contactEmail || !this.contactDesc) {
      this.messageClass = 'color-red';
      this.contactMessage = 'Write all the input fields 🤗';
      return;
    }

    // Send email using EmailJS
    emailjs
      .sendForm('service_2yin9b9', 'template_5d65b0e', '#contact-form')
      .then(
        (response: EmailJSResponseStatus) => {
          this.messageClass = 'color-blue';
          this.contactMessage = 'Message Sent ✔';

          // Clear the form
          this.contactName = '';
          this.contactEmail = '';
          this.contactDesc = '';

          // Clear the message after 5 seconds
          setTimeout(() => {
            this.contactMessage = '';
            this.messageClass = '';
          }, 5000);
        },
        (error) => {
          this.toastr.showError('Something went wrong!');
        },
      );
  }
}
