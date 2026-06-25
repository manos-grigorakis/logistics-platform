import { Component, EventEmitter, inject, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { SupplierPaymentsCreateRequest } from '../../models/supplier-payments-create-request.interface';
import { SupplierPaymentsUpdateRequest } from '../../models/supplier-payments-update-request.interface';
import { MetadataService } from '../../../../core/metadata/metadata.service';
import { SupplierPayment } from '../../models/supplier-payments.interface';
import { BehaviorSubject, finalize, Subject } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { ErrorAlert } from '../../../../shared/ui/error-alert/error-alert';
import { MainInput } from '../../../../shared/components/forms/main-input/main-input';
import { Supplier } from '../../models/supplier.interface';
import { SuppliersService } from '../../services/suppliers.service';
import { LanguageService } from '../../../../core/services/language.service';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { NgSelectComponent } from '@ng-select/ng-select';
import { PrimaryButton } from '../../../../shared/ui/primary-button/primary-button';
import { FileDropzone } from '../../../../shared/ui/file-dropzone/file-dropzone';
import { NgIcon } from '@ng-icons/core';
import { GREEK_AMOUNT_PATTERN, parseGreekAmount } from '../../../../shared/utils/currency.util';

@Component({
  selector: 'app-supplier-payments-form',
  imports: [
    TranslatePipe,
    AsyncPipe,
    LoadingSpinner,
    ErrorAlert,
    ReactiveFormsModule,
    MainInput,
    NgSelectComponent,
    PrimaryButton,
    FileDropzone,
    NgIcon,
  ],
  templateUrl: './supplier-payments-form.html',
  styleUrl: './supplier-payments-form.css',
})
export class SupplierPaymentsForm implements OnInit {
  @Input() formUsage: 'create' | 'update' = 'create';
  @Input({ required: true }) isLoading!: boolean;

  @Output() onCreate = new EventEmitter<SupplierPaymentsCreateRequest>();
  @Output() onUpdate = new EventEmitter<SupplierPaymentsUpdateRequest>();

  // Suppliers
  @ViewChild('supplierSelect') supplierSelect!: any;
  public supplierSearch$: Subject<string> = new Subject<string>();
  public suppliersLoading: boolean = false;
  public suppliers: Supplier[] = [];

  public supportedFiles: string = '.pdf, .jpeg, .png';
  public errorMessage?: string;

  private formBuilder = inject(FormBuilder);
  form = this.formBuilder.group({
    title: new FormControl<string | null>(null, Validators.required),
    description: new FormControl<string | null>(null),
    totalAmount: new FormControl<string | null>(null, [
      Validators.required,
      Validators.pattern(GREEK_AMOUNT_PATTERN),
    ]),
    paidAmount: new FormControl<string | null>(null, Validators.pattern(GREEK_AMOUNT_PATTERN)),
    type: new FormControl<string | null>(null, Validators.required),
    invoiceFile: new FormControl<File | null>(null),
    receiptFile: new FormControl<File | null>(null),
    supplierId: new FormControl<number | null>(null),
  });
  // Services
  private suppliersService = inject(SuppliersService);
  private metadataService = inject(MetadataService);
  public types$: BehaviorSubject<string[]> = this.metadataService.supplierPaymentsTypes$;
  private languageService = inject(LanguageService);

  @Input() set paymentData(value: SupplierPayment | undefined) {
    if (value) {
      this.form.patchValue({
        title: value.title,
        description: value.description,
        totalAmount: value.totalAmount.toFixed(2),
        paidAmount: value.paidAmount !== null ? value.paidAmount.toFixed(2) : null,
        type: value.type.toUpperCase(),
        supplierId: value.supplier.id,
      });
    }
  }

  // Getters
  public get title(): FormControl {
    return this.form.get('title') as FormControl;
  }

  public get description(): FormControl {
    return this.form.get('description') as FormControl;
  }

  public get totalAmount(): FormControl {
    return this.form.get('totalAmount') as FormControl;
  }

  public get paidAmount(): FormControl {
    return this.form.get('paidAmount') as FormControl;
  }

  public get type(): FormControl {
    return this.form.get('type') as FormControl;
  }

  public get invoiceFile(): FormControl {
    return this.form.get('invoiceFile') as FormControl;
  }

  public get receiptFile(): FormControl {
    return this.form.get('receiptFile') as FormControl;
  }

  public get supplierId(): FormControl {
    return this.form.get('supplierId') as FormControl;
  }

  ngOnInit(): void {
    // Dynamic validation based on form usage
    if (this.formUsage === 'create') {
      this.fetchSuppliers();
      this.supplierId.addValidators(Validators.required);
    }

    this.metadataService.fetchSupplierPaymentsTypes();
  }

  // Setters
  public setInvoiceFile(file: File): void {
    this.invoiceFile.setValue(file);
  }

  public setReceiptFile(file: File): void {
    this.receiptFile.setValue(file);
  }

  public onSubmitClick(): void {
    this.errorMessage = undefined;

    if (this.form.invalid) {
      // Mark all controls as touched & re-run validators
      Object.values(this.form.controls).forEach((control) => {
        control.markAsTouched();
        control.updateValueAndValidity({ emitEvent: true });
      });
      return;
    }

    const raw = this.form.getRawValue();

    if (raw.paidAmount !== null && Number(raw.totalAmount) < Number(raw.paidAmount)) {
      this.errorMessage = 'suppliers.fields.validation.paid-exceeds-total-amount';
      return;
    }

    if (this.formUsage === 'create') {
      this.onCreate.emit({
        title: raw.title!,
        description: raw.description !== null ? raw.description : null,
        totalAmount: parseGreekAmount(raw.totalAmount!),
        paidAmount: raw.paidAmount !== null ? parseGreekAmount(raw.paidAmount) : null,
        type: raw.type!,
        invoiceFile: raw.invoiceFile !== null ? raw.invoiceFile : null,
        receiptFile: raw.receiptFile !== null ? raw.receiptFile : null,
        supplierId: raw.supplierId!,
      });
    } else {
      this.onUpdate.emit({
        title: raw.title!,
        description: raw.description !== null ? raw.description : null,
        totalAmount: parseGreekAmount(raw.totalAmount!),
        paidAmount: raw.paidAmount !== null ? parseGreekAmount(raw.paidAmount) : null,
        type: raw.type!,
        invoiceFile: raw.invoiceFile !== null ? raw.invoiceFile : null,
        receiptFile: raw.receiptFile !== null ? raw.receiptFile : null,
      });
    }
  }

  private fetchSuppliers(): void {
    this.suppliersLoading = true;

    this.suppliersService
      .fetchSuppliers()
      .pipe(finalize(() => (this.suppliersLoading = false)))
      .subscribe({
        next: (res) => {
          this.suppliers = res.data.content;
        },
        error: (err) => {
          this.languageService.toastError(handleHttpErrors(err.status));
        },
      });
  }
}
