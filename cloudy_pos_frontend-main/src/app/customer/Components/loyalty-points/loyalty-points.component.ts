import { Component, OnInit } from '@angular/core';
import {
  Column,
  TableComponent,
} from 'app/Common/components/table/table.component';
import { ApiService } from 'app/customer/apiService/api.service';

interface LoyaltyPointsHistory {
  username: string;
  purchaseId: number | null;
  finalAmount: number | null;
  pointsEarned: number | 0;
  pointsUsed: number | 0;
  transactionType: 'EARN' | 'REDEEM';
  transactionDate: string;
}

@Component({
  selector: 'app-loyalty-points',
  imports: [TableComponent],
  templateUrl: './loyalty-points.component.html',
  styleUrls: ['./loyalty-points.component.css'],
})
export class LoyaltyPointsComponent implements OnInit {
  loyaltyPointsHistory: LoyaltyPointsHistory[] = [];
  columns: Column[] = [
    { key: 'username', label: 'Username', sortable: true },
    { key: 'purchaseId', label: 'Purchase ID', sortable: true },
    { key: 'finalAmount', label: 'Final Amount', sortable: true },
    { key: 'pointsEarned', label: 'Points Earned', sortable: true },
    { key: 'pointsUsed', label: 'Points Used', sortable: true },
    { key: 'transactionType', label: 'Transaction Type', sortable: true },
    {
      key: 'transactionDate',
      label: 'Transaction Date',
      type: 'date',
      sortable: true,
    },
  ];

  currentPage: number = 1;
  pageSize: number = 10;
  isLoading: boolean = true;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.fetchLoyaltyPointsHistory();
  }
  fetchLoyaltyPointsHistory() {
    this.isLoading = true;
    this.apiService.fetchLoyaltyPointsHistory().subscribe({
      next: (response) => {
        this.loyaltyPointsHistory = response.data.map((item: any) => ({
          username: item.username,
          purchaseId: item.purchaseId,
          finalAmount: item.finalAmount,
          pointsEarned: item.pointsEarned,
          pointsUsed: item.pointsUsed,
          transactionType: item.transactionType,
          transactionDate: item.transactionDate,
        }));
        this.isLoading = false;
      },
    });
  }
}
