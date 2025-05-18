import { Component, OnInit } from '@angular/core';
import {
  Column,
  TableComponent,
} from 'app/Common/components/table/table.component';
import { customer } from 'app/staff/model/Customer';
import { CustomerService } from 'app/staff/service/customer/customer.service';

@Component({
  selector: 'app-customer',
  imports: [TableComponent],
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.css',
})
export class CustomerComponent implements OnInit {
  allCustomers: customer[] = [];
  columns: Column[] = [
    { key: 'user_id', label: 'ID', sortable: true },
    { key: 'username', label: 'Username', sortable: true, type: 'text' },
    {
      key: 'firstName',
      label: 'First Name',
      sortable: true,
      type: 'text',
    },
    { key: 'lastName', label: 'Last Name', sortable: true, type: 'text' },
    { key: 'email', label: 'Email', sortable: true },
    { key: 'phoneNo', label: 'Phone Number' },
    { key: 'address', label: 'Address', sortable: true },
    { key: 'dob', label: 'Date of Birth', type: 'date', sortable: true },
    { key: 'createdOn', label: 'Created On', type: 'date', sortable: true },
    {
      key: 'status',
      label: 'Status',
      type: 'select',
      options: [
        { value: 'true', label: 'Active' },
        { value: 'false', label: 'Inactive' },
      ],
      sortable: true,
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

  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.loadAllCustomers();
  }

  loadAllCustomers(): void {
    this.isLoading = true;
    this.customerService.getAllCustomers().subscribe({
      next: (response) => {
        this.allCustomers = response.data || [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading customers:', err);
        this.isLoading = false;
      },
    });
  }
}
