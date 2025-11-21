import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-primary-button',
  imports: [RouterLink],
  templateUrl: './primary-button.html',
  styleUrl: './primary-button.css',
})
export class PrimaryButton {
  @Input() value!: string;
  @Input() type: string = 'submit';
  @Input() isDisabled: boolean = false;
  @Input() routerLink?: string;
}
