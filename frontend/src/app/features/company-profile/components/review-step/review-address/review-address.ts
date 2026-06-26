import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AddressReview } from '../../../model/review-step.interface';
import { ReviewFieldItem } from '../review-field-item/review-field-item';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-review-address',
  imports: [ReviewFieldItem, TranslatePipe],
  templateUrl: './review-address.html',
  styleUrl: './review-address.css',
})
export class ReviewAddress {
  @Input({ required: true }) data!: AddressReview;
  @Output() onGoToStep = new EventEmitter<number>();
}
