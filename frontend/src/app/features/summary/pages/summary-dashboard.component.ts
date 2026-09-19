import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportingApiService } from '../../../core/services/reporting-api.service';
import { NotificationApiService } from '../../../core/services/notification-api.service';
import { FileApiService } from '../../../core/services/file-api.service';
import { UserApiService } from '../../../core/services/user-api.service';
import { AuthService } from '../../../core/auth/auth.service';
import { PlatformSummary, RecentActivity } from '../../../shared/models/reporting.model';
import { NotificationItem } from '../../../shared/models/notification.model';
import { FileMetadata } from '../../../shared/models/file.model';
import { AppUser } from '../../../shared/models/user.model';
import { environment } from '../../../../environments/environment';

/**
 * Landing page tras iniciar sesión. Compone los módulos core transversales
 * que no son productos comerciales propios (Reporting, Notifications,
 * Files, gestión de usuarios — ver sección 8 del documento de arquitectura)
 * — por eso viven aquí y no tienen su propio ítem en el sidebar de productos.
 */
@Component({
  selector: 'vc-summary-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './summary-dashboard.component.html',
})
export class SummaryDashboardComponent implements OnInit {
  protected readonly companyName = environment.companyName;

  summary = signal<PlatformSummary | null>(null);
  activity = signal<RecentActivity[]>([]);
  notifications = signal<NotificationItem[]>([]);
  files = signal<FileMetadata[]>([]);
  users = signal<AppUser[]>([]);
  errorMessage = signal<string | null>(null);
  uploading = signal(false);

  readonly availableRoles = ['USER', 'ADMIN'];

  constructor(
    private reportingApi: ReportingApiService,
    private notificationApi: NotificationApiService,
    private fileApi: FileApiService,
    private userApi: UserApiService,
    protected auth: AuthService
  ) {}

  ngOnInit(): void {
    this.refreshAll();
  }

  refreshAll(): void {
    this.reportingApi.getSummary().subscribe({ next: (s) => this.summary.set(s) });
    this.reportingApi.getRecentActivity(10).subscribe({ next: (a) => this.activity.set(a) });
    this.notificationApi.list().subscribe({ next: (n) => this.notifications.set(n) });
    this.fileApi.list().subscribe({ next: (f) => this.files.set(f) });
    if (this.auth.isAdmin()) {
      this.userApi.list().subscribe({ next: (u) => this.users.set(u) });
    }
  }

  toggleRole(user: AppUser, role: string): void {
    const hasRole = user.roles.includes(role);
    const newRoles = hasRole ? user.roles.filter((r) => r !== role) : [...user.roles, role];
    if (newRoles.length === 0) {
      this.errorMessage.set('Un usuario debe tener al menos un rol.');
      return;
    }
    this.errorMessage.set(null);
    this.userApi.updateRoles(user.id, newRoles).subscribe({
      next: () => this.userApi.list().subscribe({ next: (u) => this.users.set(u) }),
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo actualizar el rol'),
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.uploading.set(true);
    this.errorMessage.set(null);
    this.fileApi.upload(file).subscribe({
      next: () => {
        this.uploading.set(false);
        input.value = '';
        this.fileApi.list().subscribe({ next: (f) => this.files.set(f) });
      },
      error: (err) => {
        this.uploading.set(false);
        this.errorMessage.set(err?.error?.message ?? 'No se pudo subir el archivo');
      },
    });
  }

  downloadFile(file: FileMetadata): void {
    this.fileApi.download(file.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = file.originalFilename;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo descargar el archivo'),
    });
  }

  deleteFile(file: FileMetadata): void {
    this.fileApi.delete(file.id).subscribe({
      next: () => this.files.set(this.files().filter((f) => f.id !== file.id)),
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo borrar el archivo'),
    });
  }

  formatSize(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }
}
