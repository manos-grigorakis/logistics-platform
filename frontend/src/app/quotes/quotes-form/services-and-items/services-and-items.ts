import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RoundedIconButton } from '../../../shared/forms/rounded-icon-button/rounded-icon-button';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MainInput } from '../../../shared/forms/main-input/main-input';
import { TitleCasePipe, LowerCasePipe } from '@angular/common';

@Component({
  selector: 'app-services-and-items',
  imports: [RoundedIconButton, ReactiveFormsModule, MainInput, TitleCasePipe, LowerCasePipe],
  templateUrl: './services-and-items.html',
  styleUrl: './services-and-items.css',
})
export class ServicesAndItems {
  @Input() parentForm!: FormGroup;
  @Input() items!: FormArray<FormGroup>;
  @Input() quoteItemUnits!: string[];
  @Output() onAddItem = new EventEmitter<void>();
  @Output() onRemoveItem = new EventEmitter<number>();

  public onAddItemClick(): void {
    this.onAddItem.emit();
  }

  public onRemoveItemClick(id: number): void {
    this.onRemoveItem.emit(id);
  }

  public getUnitControl(index: number): FormControl<string | null> {
    return this.items.at(index).get('unit') as FormControl<string | null>;
  }
}
