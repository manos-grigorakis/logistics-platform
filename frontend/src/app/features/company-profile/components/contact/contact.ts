import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { MainInput } from '../../../../shared/components/forms/main-input/main-input';
import { RoundedIconButton } from '../../../../shared/components/forms/rounded-icon-button/rounded-icon-button';

@Component({
  selector: 'app-contact',
  imports: [ReactiveFormsModule, TranslatePipe, MainInput, RoundedIconButton],
  templateUrl: './contact.html',
  styleUrl: './contact.css',
})
export class Contact {
  @Input({ required: true }) parentForm!: FormGroup;
  @Input({ required: true }) parentFormArray!: FormArray;

  @Output() onAddPhone = new EventEmitter<void>();
  @Output() onRemovePhone = new EventEmitter<number>();

  public get phones(): FormArray<FormControl<string>> {
    return this.parentForm.get('phones') as FormArray<FormControl<string>>;
  }

  public onAddPhoneClick(): void {
    this.onAddPhone.emit();
  }

  public onRemovePhoneClick(index: number): void {
    this.onRemovePhone.emit(index);
  }
}
