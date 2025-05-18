import { CommonModule } from '@angular/common';
import {
  Component,
  ElementRef,
  OnInit,
  ViewChild,
  AfterViewInit,
  ChangeDetectorRef,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon'; // Import MatIconModule
import { AnimationService } from 'app/Common/services/animation.service';
import { ProfileService } from 'app/customer/apiService/customer/profile.service';
import { Customer } from 'app/customer/Model/customer';
import { AuthService } from 'app/Service/auth/auth.service';
import { ToastService } from 'app/Service/toast/toast.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatIconModule, // Add MatIconModule to imports
    ReactiveFormsModule,
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit, AfterViewInit {
  customer: Customer = {} as Customer;
  customerForm!: FormGroup;
  changePasswordForm!: FormGroup;
  showOldPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;

  @ViewChild('profileCard', { static: false }) profileCard!: ElementRef;
  @ViewChild('formContainer', { static: false }) formContainer!: ElementRef;
  @ViewChild('changePasswordContainer', { static: false })
  changePasswordContainer!: ElementRef;

  constructor(
    private animationService: AnimationService,
    private fb: FormBuilder,
    private profileService: ProfileService,
    private toastr: ToastService,
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    console.log('ProfileComponent initialized');
    this.loadCustomerProfile();
    this.initChangePasswordForm();
  }

  ngAfterViewInit(): void {
    this.animateProfileCard();
    this.animateFormContainer();
    this.animateChangePasswordContainer();
  }

  loadCustomerProfile(): void {
    this.profileService.loadProfile().subscribe({
      next: (data: Customer) => {
        this.customer = {
          ...data,
          profilePhoto: data.profilePhoto || '',
          firstName: data.firstName || '',
          lastName: data.lastName || '',
          phoneNo: data.phoneNo || '',
          address: data.address || '',
          dob: data.dob || '',
        };
        this.initializeForm();
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load profile:', error);
        this.toastr.showError('Failed to load profile');
      },
    });
  }

  initializeForm(): void {
    this.customerForm = this.fb.group({
      firstName: [this.customer.firstName, [Validators.required]],
      lastName: [this.customer.lastName, [Validators.required]],
      email: [this.customer.email, [Validators.required, Validators.email]],
      phoneNo: [
        this.customer.phoneNo,
        [Validators.required, Validators.pattern(/^\d{10}$/)],
      ],
      address: [this.customer.address, [Validators.required]],
      dob: [this.customer.dob, [Validators.required]],
    });
  }

  animateProfileCard(): void {
    if (this.profileCard) {
      this.animationService.slideInFromLeft(this.profileCard.nativeElement);
    }
  }

  animateFormContainer(): void {
    if (this.formContainer) {
      this.animationService.fadeIn(this.formContainer.nativeElement);
    }
  }

  animateChangePasswordContainer(): void {
    if (this.changePasswordContainer) {
      this.animationService.fadeIn(this.changePasswordContainer.nativeElement);
    }
  }

  uploadPic(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      const file = fileInput.files[0];
      this.profileService
        .uploadProfilePicture(this.customer.user_id, file)
        .subscribe({
          next: (res) => {
            console.log('Upload successful', res);
            this.customer.profilePhoto = res.imageUrl;
            this.toastr.showSuccess('Profile photo updated!');
            this.cdr.detectChanges();
            this.loadCustomerProfile();
          },
          error: (err) => {
            console.error('Upload failed', err);
            this.toastr.showError('Failed to upload photo');
          },
        });
    }
  }

  removePic(imgUrl: string): void {
    // const imgUrl = this.customer.profilePhoto
    //   ? this.customer.profilePhoto.split('/').pop() || ''
    //   : '';
    this.profileService
      .deleteProfilePicture(this.customer.user_id, imgUrl)
      .subscribe({
        next: () => {
          console.log('Profile picture removed');
          this.customer.profilePhoto = '';
          this.toastr.showSuccess('Profile picture removed');
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error removing profile picture:', error);
          this.toastr.showError('Failed to remove profile picture');
        },
      });
  }

  updateProfile(): void {
    if (this.customerForm.invalid) {
      console.error('Form is invalid:', this.customerForm.value);
      this.toastr.showError('Please fill out all required fields correctly');
      this.customerForm.markAllAsTouched();
      return;
    }

    const updatedData: Customer = {
      user_id: this.customer.user_id,
      username: this.customer.username,
      profilePhoto: this.customer.profilePhoto,
      firstName: this.customerForm.value.firstName,
      lastName: this.customerForm.value.lastName,
      email: this.customerForm.value.email,
      phoneNo: this.customerForm.value.phoneNo,
      address: this.customerForm.value.address,
      dob: this.customerForm.value.dob,
    };

    this.profileService.updateProfile(updatedData).subscribe({
      next: () => {
        this.toastr.showSuccess('Profile updated successfully');
        this.customer = updatedData;
        this.loadCustomerProfile();
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        this.toastr.showError('Failed to update profile');
      },
    });
  }

  private initChangePasswordForm(): void {
    this.changePasswordForm = this.fb.group(
      {
        oldPassword: this.fb.control('', {
          validators: [Validators.required],
        }),
        newPassword: this.fb.control('', {
          validators: [
            Validators.required,
            Validators.minLength(6),
            Validators.maxLength(20),
            Validators.pattern(/^(?=.*\d)(?=.*[a-z]).{6,20}$/),
          ],
        }),
        confirmPassword: this.fb.control('', {
          validators: [Validators.required],
        }),
      },
      { validators: this.passwordMatchValidator },
    );
  }

  private passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    return newPassword === confirmPassword ? null : { mismatch: true };
  }

  get oldPassword() {
    return this.changePasswordForm.get('oldPassword');
  }

  get newPassword() {
    return this.changePasswordForm.get('newPassword');
  }

  get confirmPassword() {
    return this.changePasswordForm.get('confirmPassword');
  }

  togglePasswordVisibility(
    field: 'oldPassword' | 'newPassword' | 'confirmPassword',
  ): void {
    if (field === 'oldPassword') {
      this.showOldPassword = !this.showOldPassword;
    } else if (field === 'newPassword') {
      this.showNewPassword = !this.showNewPassword;
    } else {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }

  onSubmit(): void {
    if (this.changePasswordForm.valid) {
      const { oldPassword, newPassword } = this.changePasswordForm.value;
      this.authService
        .changePass(this.customer.user_id, oldPassword, newPassword)
        .subscribe({
          next: (response) => {
            this.toastr.showSuccess('Password changed successfully');
            this.changePasswordForm.reset();
          },
          error: (error) => {
            console.error('Error changing password:', error);
            this.toastr.showError('Failed to change password');
          },
        });
    } else {
      this.changePasswordForm.markAllAsTouched();
    }
  }
}
