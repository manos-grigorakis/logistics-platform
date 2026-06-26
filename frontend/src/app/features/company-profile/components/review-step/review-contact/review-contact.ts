import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ContactReview } from '../../../model/review-step.interface';
import { TranslatePipe } from '@ngx-translate/core';
import { ReviewFieldItem } from '../review-field-item/review-field-item';

@Component({
  selector: 'app-review-contact',
  imports: [TranslatePipe, ReviewFieldItem],
  templateUrl: './review-contact.html',
  styleUrl: './review-contact.css',
})
export class ReviewContact {
  @Input({ required: true }) data!: ContactReview;
  @Output() onGoToStep = new EventEmitter<number>();
}
