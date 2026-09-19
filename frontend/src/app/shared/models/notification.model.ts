export interface NotificationItem {
  id: string;
  channel: string;
  recipient?: string;
  subject: string;
  body?: string;
  status: string;
  createdAt: string;
  sentAt?: string;
}
