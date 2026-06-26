import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-review-field-item',
  imports: [],
  templateUrl: './review-field-item.html',
  styleUrl: './review-field-item.css',
})
export class ReviewFieldItem {
  @Input({ required: true }) label!: string;
  @Input({ required: true }) value!: string | null;
}
