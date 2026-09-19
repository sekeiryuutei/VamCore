export interface FileMetadata {
  id: string;
  originalFilename: string;
  contentType?: string;
  sizeBytes: number;
  uploadedBy?: string;
  createdAt: string;
}
