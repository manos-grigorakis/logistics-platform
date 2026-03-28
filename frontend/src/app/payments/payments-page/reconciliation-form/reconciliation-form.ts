import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { PrimaryButton } from '../../../shared/ui/primary-button/primary-button';
import { NgIcon } from '@ng-icons/core';
import { FileDropzone } from '../../../shared/ui/file-dropzone/file-dropzone';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { NgSelectComponent } from '@ng-select/ng-select';
import { Customer } from '../../../customers/models/customer';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-reconciliation-form',
  imports: [PrimaryButton, NgIcon, FileDropzone, NgSelectComponent, ReactiveFormsModule],
  templateUrl: './reconciliation-form.html',
  styleUrl: './reconciliation-form.css',
})
export class ReconciliationForm {
  @Input({ required: true }) form!: FormGroup;
  @Input({ required: true }) customersList!: Customer[];
  @Input({ required: true }) customersLoading?: boolean;
  @Input({ required: true }) customerSearch$!: Subject<string>;
  @Output() formSubmit = new EventEmitter<void>();

  @ViewChild('customerSelect') customerSelect!: any;

  public onSubmit(): void {
    this.formSubmit.emit();
  }

  public onClickFocusCustomersSelect(): void {
    this.customerSelect.focus();
  }

  public get customerId(): FormControl {
    return this.form.get('customerId') as FormControl;
  }

  public get invoiceFile(): FormControl {
    return this.form.get('invoiceFile') as FormControl;
  }

  public get bankStatementFile(): FormControl {
    return this.form.get('bankStatementFile') as FormControl;
  }

  public onInvoicesFileSelected(file: File): void {
    this.invoiceFile.setValue(file);
  }

  public onBankFileSelected(file: File): void {
    this.bankStatementFile.setValue(file);
  }
}
