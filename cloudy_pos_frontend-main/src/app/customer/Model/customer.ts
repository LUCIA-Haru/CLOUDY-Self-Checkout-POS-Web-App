export interface Customer {
  profilePhoto: string | null;
  user_id: number;
  guid?: string; // Optional, as it's not used in the form
  username: string;
  firstName: string | null;
  lastName: string | null;
  email: string;
  phoneNo: string | null;
  address: string | null;
  dob: string | null;
  createdOn?: string; // Optional
  updatedOn?: string | null; // Optional
  status?: boolean; // Optional
  role?: string; // Optional
}
