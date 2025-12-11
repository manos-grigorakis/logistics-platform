import { Component, Input, ViewChild } from '@angular/core';
import { MainInput } from '../../../shared/forms/main-input/main-input';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { NgSelectComponent } from '@ng-select/ng-select';
import { Subject } from 'rxjs';
import { CustomerSummary } from '../../models/customer-summary';

@Component({
  selector: 'app-basic-information',
  imports: [MainInput, ReactiveFormsModule, NgSelectComponent],
  templateUrl: './basic-information.html',
  styleUrl: './basic-information.css',
})
export class BasicInformation {
  @Input() parentForm!: FormGroup;
  @Input() customerSearch$!: Subject<string>;
  @Input() customersLoading?: boolean;
  @Input() customersList: CustomerSummary[] = [];

  @ViewChild('customerSelect') customerSelect!: any;

  public customerSearchCtrl = new FormControl();

  public get customerId(): FormControl {
    return this.parentForm.get('customerId') as FormControl;
  }

  public onClickFocusCustomersSelect(): void {
    this.customerSelect.focus();
  }
}
