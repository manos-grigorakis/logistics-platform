import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { NgClass } from '@angular/common';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-file-dropzone',
  imports: [NgIcon, NgClass],
  templateUrl: './file-dropzone.html',
  styleUrl: './file-dropzone.css',
})
export class FileDropzone {
  @Input() showMaxFileSize: boolean = true;
  @Input() supportedFiles?: string;
  @Input() maxFileSizeInMB?: number;
  @Output() fileSelected = new EventEmitter<File>();

  public isFileSelected: boolean = false;
  public isFileLoading: boolean = false;
  public error: boolean = false;
  public fileName?: string = undefined;

  public onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.error = false;

    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      this.fileValidation(file);
      if (this.error) return;

      this.isFileLoading = true;
      this.isFileSelected = true;
      this.fileName = file.name;
      this.fileSelected.emit(file);
    }
  }

  private fileValidation(file: File) {
    this.error = false;

    if (!this.validateFileSize(file.size)) {
      this.error = true;
      toast.error('File too large');
      return;
    }

    if (!this.validateFileType(file)) {
      this.error = true;
      toast.error('Unsupported file type');
      return;
    }
  }

  private validateFileSize(size: number): boolean {
    if (!this.showMaxFileSize || !this.maxFileSizeInMB) return true;

    const maxBytes = this.maxFileSizeInMB * 1024 * 1024;
    return size <= maxBytes;
  }

  private validateFileType(file: File): boolean {
    if (!this.supportedFiles) return true;

    const extension = '.' + file.name.split('.').pop()?.toLowerCase();
    const accepted = this.supportedFiles.split(',').map((e) => e.trim().toLowerCase());
    return accepted.includes(extension);
  }
}
