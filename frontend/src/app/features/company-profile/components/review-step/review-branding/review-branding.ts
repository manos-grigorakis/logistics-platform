import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  SimpleChanges,
} from '@angular/core';
import { BrandingReview } from '../../../model/review-step.interface';
import { TranslatePipe } from '@ngx-translate/core';
import { ReviewFieldItem } from '../review-field-item/review-field-item';

@Component({
  selector: 'app-review-branding',
  imports: [TranslatePipe, ReviewFieldItem],
  templateUrl: './review-branding.html',
  styleUrl: './review-branding.css',
})
export class ReviewBranding implements OnChanges, OnDestroy {
  @Input({ required: true }) data!: BrandingReview;
  @Output() onGoToStep = new EventEmitter<number>();

  public logoPreviewUrl: string | null = null;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data']) {
      this.revokePreviousUrl();
      this.logoPreviewUrl = this.data.logoFile ? URL.createObjectURL(this.data.logoFile) : null;
    }
  }

  ngOnDestroy(): void {
    this.revokePreviousUrl();
  }

  private revokePreviousUrl(): void {
    if (this.logoPreviewUrl) {
      URL.revokeObjectURL(this.logoPreviewUrl);
    }
  }
}
