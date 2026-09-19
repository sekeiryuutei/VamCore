import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { FileMetadata } from '../../shared/models/file.model';

@Injectable({ providedIn: 'root' })
export class FileApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/files`;

  constructor(private http: HttpClient) {}

  upload(file: File): Observable<FileMetadata> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<FileMetadata>(this.baseUrl, formData);
  }

  list(): Observable<FileMetadata[]> {
    return this.http.get<FileMetadata[]>(this.baseUrl);
  }

  /** Descarga como blob: el endpoint requiere el JWT, así que un <a href> plano no sirve. */
  download(fileId: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${fileId}/download`, { responseType: 'blob' });
  }

  delete(fileId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${fileId}`);
  }
}
