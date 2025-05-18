import { Column } from './../../../../Common/components/table/table.component';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { TableComponent } from 'app/Common/components/table/table.component';
import { ToastService } from 'app/Service/toast/toast.service';
import { Staff } from 'app/staff/model/Staff';
import { StaffService } from 'app/staff/service/staff.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-staff',
  imports: [TableComponent],
  templateUrl: './staff.component.html',
  styleUrls: ['./staff.component.css'],
})
export class StaffComponent implements OnInit {
  additionalArgument: any;
  allStaff: Staff[] = [];
  columns: Column[] = [
    { key: 'user_id', label: 'ID', sortable: true, editable: false },
    {
      key: 'username',
      label: 'Username',
      sortable: true,
      type: 'text',
      required: true,
    },
    {
      key: 'firstName',
      label: 'First Name',
      sortable: true,
      type: 'text',
      required: true,
    },
    {
      key: 'lastName',
      label: 'Last Name',
      sortable: true,
      type: 'text',
      required: true,
    },
    {
      key: 'email',
      label: 'Email',
      sortable: true,
      required: true,
    },
    { key: 'password', label: 'Password', hidden: true },
    { key: 'phoneNo', label: 'Phone Number', required: true },
    { key: 'address', label: 'Address', sortable: true, required: true },
    { key: 'dob', label: 'Date of Birth', type: 'date', sortable: true },
    {
      key: 'department',
      label: 'Department',
      type: 'text',
      sortable: true,
      required: true,
    },
    { key: 'position', label: 'Position', type: 'text', sortable: true },
    { key: 'role', label: 'Role', type: 'text', sortable: true },
    { key: 'createdOn', label: 'Created On', type: 'date', editable: false },
    { key: 'createdBy', label: 'Created By', type: 'text', editable: false },
    { key: 'updatedOn', label: 'Updated On', type: 'date', editable: false },
    {
      key: 'status',
      label: 'Status',
      type: 'select',
      options: [
        { value: 'true', label: 'Active' },
        { value: 'false', label: 'Inactive' },
      ],
      sortable: true,
      required: true,
    },
  ];
  filterConfig = {
    column: 'status',
    options: [
      { value: 'all', label: 'All' },
      { value: 'true', label: 'Active' },
      { value: 'false', label: 'Inactive' },
    ],
  };

  currentPage: number = 1;
  pageSize: number = 10;
  isLoading: boolean = true;
  errorMessage: string | null = null;

  private pollingSubscription: Subscription | null = null;

  constructor(
    private staffService: StaffService,
    private cdr: ChangeDetectorRef,
    private toastrService: ToastService,
  ) {
    // this.fetchStaffData();
  }

  ngOnInit(): void {
    this.fetchStaffData();
  }

  fetchStaffData(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.staffService.getAllStaff().subscribe({
      next: (response) => {
        this.allStaff = response.data?.content || response.content || [];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage =
          err.error.message || 'An error occurred while fetching staff data.';
        this.toastrService.showError('Error fetching staff data');
      },
      complete: () => {
        console.log('Staff data fetch complete');
      },
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.cdr.detectChanges();
    this.fetchStaffData();
  }

  addStaff(newStaff: Staff): void {
    const staffData: Staff = {
      username: newStaff.username,
      firstName: newStaff.firstName,
      lastName: newStaff.lastName,
      email: newStaff.email,
      password: newStaff.password,
      phoneNo: newStaff.phoneNo,
      address: newStaff.address,
      dob: newStaff.dob,
      department: newStaff.department,
      position: newStaff.position,
      role: newStaff.role,
    };
    this.staffService.addStaff(staffData).subscribe({
      next: (response) => {
        this.allStaff.push(response.data);
        this.toastrService.showSuccess('Staff added successfully');
        this.cdr.detectChanges();
        this.fetchStaffData();
      },
      error: (err) => {
        this.toastrService.showError('Error adding staff');
      },
    });
  }
  updateStaff(updatedStaff: Staff, id: number): void {
    debugger;
    if (!id) {
      console.error('Invalid user_id for update');
      this.toastrService.showError('Cannot update staff: Invalid user_id');
      return;
    }
    const staffData: Staff = {
      user_id: id,
      username: updatedStaff.username,
      firstName: updatedStaff.firstName ?? null,
      lastName: updatedStaff.lastName ?? null,
      email: updatedStaff.email ?? null,
      password: updatedStaff.password ?? null,
      phoneNo: updatedStaff.phoneNo ?? null,
      address: updatedStaff.address ?? null,
      dob: updatedStaff.dob ?? null,
      department: updatedStaff.department ?? null,
      position: updatedStaff.position ?? null,
      role: updatedStaff.role ?? null,
      status:
        String(updatedStaff.status) === 'true' || updatedStaff.status === true,
    };
    console.log('Updating staff with ID:', id, 'Data:', staffData);

    this.staffService.updateStaff(staffData, id).subscribe({
      next: (response) => {
        this.allStaff = this.allStaff.map((staff) =>
          staff.user_id === id ? response.data : staff,
        );
        this.cdr.detectChanges();
        this.toastrService.showSuccess('Staff updated successfully');
        this.fetchStaffData();
      },
    });
  }

  removeStaff(staff: Staff): void {
    debugger;
    if (!staff.user_id) {
      console.error('Invalid user_id for deletion');
      this.toastrService.showError('Cannot delete staff: Invalid user_id');
      return;
    }
    this.staffService.deleteStaff(staff.user_id).subscribe({
      next: (response) => {
        this.allStaff = this.allStaff.filter(
          (staff) => staff.user_id !== staff.user_id,
        );
        this.cdr.detectChanges();
        this.toastrService.showSuccess('Staff deleted successfully');
        this.fetchStaffData();
      },
      error: (err) => {
        this.toastrService.showError('Error deleting staff');
        this.cdr.detectChanges();
      },
    });
  }
}
