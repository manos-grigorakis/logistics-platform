import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-primary-button',
  imports: [],
  templateUrl: './primary-button.html',
  styleUrl: './primary-button.css',
})
export class PrimaryButton {
  @Input() value!: string;
  @Input() type: string = 'submit';
  @Input() isDisabled: boolean = false;
}
