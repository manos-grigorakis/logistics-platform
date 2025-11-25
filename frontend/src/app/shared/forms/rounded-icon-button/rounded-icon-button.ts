import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgIcon } from '@ng-icons/core';

@Component({
  selector: 'app-rounded-icon-button',
  imports: [NgIcon, NgClass],
  templateUrl: './rounded-icon-button.html',
  styleUrl: './rounded-icon-button.css',
})
export class RoundedIconButton {
  @Output() clicked = new EventEmitter<void>();
  @Input() isDisabled?: boolean;
  @Input() icon?: string;
  @Input() iconSize: string = '16';
  @Input() color: 'info' | 'success' | 'warning' | 'danger' | 'primary' = 'primary';

  public onClick() {
    if (this.isDisabled) return;
    this.clicked.emit();
  }

  public get appliedColor() {
    switch (this.color) {
      case 'info':
        return 'bg-info hover:bg-info-dark';
      case 'success':
        return 'bg-success hover:bg-success-dark';
      case 'warning':
        return 'bg-warning hover:bg-warning-dark';
      case 'danger':
        return 'bg-danger hover:bg-danger-dark';
      case 'primary':
      default:
        return 'bg-primary-400 hover:bg-primary-500';
    }
  }
}
