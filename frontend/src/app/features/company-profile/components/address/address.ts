import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { MainInput } from '../../../../shared/components/forms/main-input/main-input';

@Component({
  selector: 'app-address',
  imports: [ReactiveFormsModule, TranslatePipe, MainInput],
  templateUrl: './address.html',
  styleUrl: './address.css',
})
export class Address {
  @Input({ required: true }) parentForm!: FormGroup;
}
