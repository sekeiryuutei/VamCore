export interface AppUser {
  id: string;
  tenantId: string;
  email: string;
  fullName?: string;
  status: string;
  roles: string[];
}
