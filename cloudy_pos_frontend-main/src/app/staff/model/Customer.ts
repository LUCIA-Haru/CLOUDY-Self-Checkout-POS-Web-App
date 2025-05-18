export interface customer {
  user_id?: number;
  guid?: string;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNo: string;
  address: string;
  dob: Date;
  status: boolean;
  createdOn?: string;
  createdBy?: string;
  updatedOn?: string | null;
  updatedBy?: string | null;
}
