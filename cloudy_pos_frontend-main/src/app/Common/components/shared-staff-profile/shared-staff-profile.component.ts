import { CommonModule } from '@angular/common';
import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  ViewChild,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { AnimationService } from 'app/Common/services/animation.service';
import { ProfileService } from 'app/customer/apiService/customer/profile.service';
import { AuthService } from 'app/Service/auth/auth.service';
import { ToastService } from 'app/Service/toast/toast.service';
import { Staff } from 'app/staff/model/Staff';

@Component({
  selector: 'app-shared-staff-profile',
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatIconModule,
    ReactiveFormsModule,
  ],
  templateUrl: './shared-staff-profile.component.html',
  styleUrl: './shared-staff-profile.component.css',
})
export class SharedStaffProfileComponent {
  staff: Staff = {
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phoneNo: '',
    address: '',
    dob: new Date(),
    department: '',
    role: 'staff',
    position: '',
  } as Staff;
  changePasswordForm!: FormGroup;
  showOldPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;

  @ViewChild('profileCard', { static: false }) profileCard!: ElementRef;
  @ViewChild('infoContainer', { static: false }) infoContainer!: ElementRef;
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
    console.log('SharedStaffProfileComponent initialized');
    this.loadStaffProfile();
    this.initChangePasswordForm();
  }

  ngAfterViewInit(): void {
    this.animateProfileCard();
    this.animateInfoContainer();
    this.animateChangePasswordContainer();
  }

  loadStaffProfile(): void {
    this.profileService.loadStaffProfile().subscribe({
      next: (data: Staff) => {
        this.staff = {
          ...data,
          profilePhoto: data.profilePhoto || '',
          firstName: data.firstName || '',
          lastName: data.lastName || '',
          email: data.email || '',
          phoneNo: data.phoneNo || '',
          address: data.address || '',
          dob: data.dob || new Date(),
          role: data.role || 'staff',
          department: data.department || '',
          position: data.position || '',
          status: data.status ?? false,
          username: data.username || '',
          password: data.password || '',
        };
        this.cdr.detectChanges();
      },
      error: (error: any) => {
        console.error('Failed to load staff profile:', error);
        this.toastr.showError('Failed to load profile');
      },
    });
  }

  animateProfileCard(): void {
    if (this.profileCard) {
      this.animationService.slideInFromLeft(this.profileCard.nativeElement);
    }
  }

  animateInfoContainer(): void {
    if (this.infoContainer) {
      this.animationService.fadeIn(this.infoContainer.nativeElement);
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
        .uploadStaffProfilePicture(this.staff.user_id!, file)
        .subscribe({
          next: (res: any) => {
            console.log('Upload successful', res);
            this.staff.profilePhoto = res.imageUrl;
            this.toastr.showSuccess('Profile photo updated!');
            this.cdr.detectChanges();
            this.loadStaffProfile();
          },
          error: (err: any) => {
            console.error('Upload failed', err);
            this.toastr.showError('Failed to upload photo');
          },
        });
    }
  }

  removePic(imgUrl: string): void {
    // const imgUrl = this.staff.profilePhoto
    //   ? this.staff.profilePhoto.split('/').pop() || ''
    //   : '';
    this.profileService
      .deleteStaffProfilePicture(this.staff.user_id!, imgUrl)
      .subscribe({
        next: () => {
          console.log('Profile picture removed');
          this.staff.profilePhoto = '';
          this.toastr.showSuccess('Profile picture removed');
          this.cdr.detectChanges();
        },
        error: (error: any) => {
          console.error('Error removing profile picture:', error);
          this.toastr.showError('Failed to remove profile picture');
        },
      });
  }

  private initChangePasswordForm(): void {
    this.changePasswordForm = this.fb.group(
      {
        oldPassword: ['', [Validators.required]],
        newPassword: [
          '',
          [
            Validators.required,
            Validators.minLength(6),
            Validators.maxLength(20),
            Validators.pattern(/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,20}$/),
          ],
        ],
        confirmPassword: ['', [Validators.required]],
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
        .changePass(this.staff.user_id!, oldPassword, newPassword)
        .subscribe({
          next: () => {
            this.toastr.showSuccess('Password changed successfully');
            this.changePasswordForm.reset();
          },
          error: (error: any) => {
            console.error('Error changing password:', error);
            this.toastr.showError('Failed to change password');
          },
        });
    } else {
      this.changePasswordForm.markAllAsTouched();
    }
  }
}
