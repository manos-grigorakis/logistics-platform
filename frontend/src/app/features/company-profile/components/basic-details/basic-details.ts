import { Component, Input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { MainInput } from '../../../../shared/components/forms/main-input/main-input';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-basic-details',
  imports: [TranslatePipe, MainInput, ReactiveFormsModule],
  templateUrl: './basic-details.html',
  styleUrl: './basic-details.css',
})
export class BasicDetails {
  @Input() tinNumber?: string;
  @Input({ required: true }) parentForm!: FormGroup;
  @Input() formUsage: 'create' | 'update' = 'create';
}
