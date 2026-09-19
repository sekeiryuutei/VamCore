export interface AuthSession {
  accessToken: string;
  tokenType: string;
}

export interface CurrentUser {
  id: string;
  tenantId: string;
  email: string;
  fullName?: string;
  roles: string[];
}
