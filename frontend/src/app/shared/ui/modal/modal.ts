import {
  Component,
  EventEmitter,
  HostListener,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { NgClass } from '@angular/common';
import { NgIcon } from '@ng-icons/core';
import { LanguageService } from '../../services/language.service';
import { take } from 'rxjs';

@Component({
  selector: 'app-modal',
  imports: [NgClass, NgIcon],
  templateUrl: './modal.html',
  styleUrl: './modal.css',
})
export class Modal implements OnInit {
  @Input() isEnabled?: boolean;
  @Input() icon: string = 'lucideInfo';
  @Input() iconSize: string = '28';
  @Input() color: 'info' | 'success' | 'warning' | 'danger' = 'info';
  @Input() confirmButtonColor: 'info' | 'success' | 'warning' | 'danger' = 'danger';
  @Input() header?: string;
  @Input() message?: string;
  @Input() confirmButtonText: string = '';
  @Input() cancelButtonText: string = '';
  @Input() showCancelButton: boolean = true;

  @Output() closeModalClick = new EventEmitter<void>();
  @Output() confirmClick = new EventEmitter<void>();
  @Output() cancelClick = new EventEmitter<void>();

  private languageService = inject(LanguageService);

  ngOnInit(): void {
    if (!this.confirmButtonText) {
      this.languageService
        .translateKeyAsync('common.actions.yes-sure')
        .pipe(take(1))
        .subscribe((val) => (this.confirmButtonText = val));
    }

    if (!this.cancelButtonText) {
      this.languageService
        .translateKeyAsync('common.actions.no-cancel')
        .pipe(take(1))
        .subscribe((val) => (this.cancelButtonText = val));
    }
  }

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

  public get confirmButtonColorContainer(): string {
    switch (this.confirmButtonColor) {
      case 'success':
        return 'bg-success hover:bg-success-dark focus:ring-success-light';
      case 'warning':
        return 'bg-warning hover:bg-warning-dark focus:ring-warning-light';
      case 'info':
        return 'bg-info hover:bg-info-dark focus:ring-info-light';
      case 'danger':
        return 'bg-danger hover:bg-danger-dark focus:ring-danger-light';
    }
  }
}
