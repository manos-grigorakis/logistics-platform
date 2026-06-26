import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import {
  AddressReview,
  BasicDetailsReview,
  BrandingReview,
  ContactReview,
} from '../../model/review-step.interface';
import { ReviewAddress } from './review-address/review-address';
import { ReviewBasicDetails } from './review-basic-details/review-basic-details';
import { ReviewContact } from './review-contact/review-contact';
import { ReviewBranding } from './review-branding/review-branding';

@Component({
  selector: 'app-review-step',
  imports: [ReactiveFormsModule, ReviewAddress, ReviewBasicDetails, ReviewContact, ReviewBranding],
  templateUrl: './review-step.html',
  styleUrl: './review-step.css',
})
export class ReviewStep {
  @Input({ required: true }) basicDetails!: BasicDetailsReview;
  @Input({ required: true }) address!: AddressReview;
  @Input({ required: true }) contact!: ContactReview;
  @Input({ required: true }) branding!: BrandingReview;

  @Output() onGoToStep = new EventEmitter<number>();
}
