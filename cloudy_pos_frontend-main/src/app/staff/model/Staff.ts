export interface Staff {
  user_id?: number;
  guid?: string;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phoneNo: string;
  address: string;
  dob: Date;
  status?: boolean;
  department: string;
  role: string;
  position: string;
  createdOn?: string;
  createdBy?: string;
  updatedOn?: string | null;
  updatedBy?: string | null;
  profilePhoto?: string | null;
}
