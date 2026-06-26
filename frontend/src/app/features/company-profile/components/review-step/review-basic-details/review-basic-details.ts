import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { BasicDetailsReview } from '../../../model/review-step.interface';
import { ReviewFieldItem } from '../review-field-item/review-field-item';

@Component({
  selector: 'app-review-basic-details',
  imports: [TranslatePipe, ReviewFieldItem],
  templateUrl: './review-basic-details.html',
  styleUrl: './review-basic-details.css',
})
export class ReviewBasicDetails {
  @Input({ required: true }) data!: BasicDetailsReview;
  @Output() onGoToStep = new EventEmitter<number>();
}
