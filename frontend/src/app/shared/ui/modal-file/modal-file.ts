import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  Output,
  ElementRef,
  ViewChild,
} from '@angular/core';
import { NgClass } from '@angular/common';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { NgIcon } from '@ng-icons/core';

@Component({
  selector: 'app-modal-file',
  imports: [NgClass, PdfViewerModule, NgIcon],
  templateUrl: './modal-file.html',
  styleUrl: './modal-file.css',
})
export class ModalFile {
  @Input() pdfUrl?: string;
  @Input() isEnabled?: boolean;
  @Input() pdfName: string = '';
  @Input() showSendViaMailButton: boolean = true;
  @Output() closeModal = new EventEmitter<void>();
  @Output() onSendMail = new EventEmitter<void>();

  @ViewChild('modalCard') modalCard!: ElementRef;

  public onCloseModalClick(): void {
    this.closeModal.emit();
  }

  @HostListener('document:keydown', ['$event'])
  public onEscPress(event: KeyboardEvent): void {
    const keyboardEvent = event as KeyboardEvent;

    if (keyboardEvent.key === 'Escape' && this.isEnabled) {
      event.preventDefault();
      this.closeModal.emit();
    }
  }

  public onSendMailClick(): void {
    this.onSendMail.emit();
  }

  public downloadPdf(): void {
    fetch(this.pdfUrl!)
      .then((res) => res.blob())
      .then((blob) => {
        const downloadUrl = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = `${this.pdfName}.pdf`;
        link.click();
        URL.revokeObjectURL(downloadUrl);
      });
  }
}
