import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core';
import { NgClass } from '@angular/common';
import { NgIcon } from '@ng-icons/core';

@Component({
  selector: 'app-modal',
  imports: [NgClass, NgIcon],
  templateUrl: './modal.html',
  styleUrl: './modal.css',
})
export class Modal {
  @Input() isEnabled?: boolean;
  @Input() icon: string = 'lucideInfo';
  @Input() iconSize: string = '28';
  @Input() color: 'info' | 'success' | 'warning' | 'danger' = 'info';
  @Input() header?: string;
  @Input() message?: string;
  @Input() confirmButtonText: string = "Yes, I'm sure";
  @Input() cancelButtonText: string = 'No, cancel';

  @Output() closeModalClick = new EventEmitter<void>();
  @Output() confirmClick = new EventEmitter<void>();
  @Output() cancelClick = new EventEmitter<void>();

  public onCloseModalClick(): void {
    this.closeModalClick.emit();
  }

  public onConfirmClick(): void {
    this.confirmClick.emit();
  }

  public onCancelClick(): void {
    this.cancelClick.emit();
  }

  @HostListener('document:keydown', ['$event'])
  public onEscPress(event: KeyboardEvent): void {
    const keyboardEvent = event as KeyboardEvent;

    if (keyboardEvent.key === 'Escape' && this.isEnabled) {
      event.preventDefault();
      this.cancelClick.emit();
    }
  }

  public get iconColorContainer(): string {
    switch (this.color) {
      case 'success':
        return 'bg-success-light';
      case 'warning':
        return 'bg-warning-light';
      case 'danger':
        return 'bg-danger-light';
      case 'info':
      default:
        return 'bg-info-light';
    }
  }

  public get iconColor(): string {
    switch (this.color) {
      case 'success':
        return 'var(--color-success)';
      case 'warning':
        return 'var(--color-warning)';
      case 'danger':
        return 'var(--color-danger)';
      case 'info':
      default:
        return 'var(--color-info)';
    }
  }
}
