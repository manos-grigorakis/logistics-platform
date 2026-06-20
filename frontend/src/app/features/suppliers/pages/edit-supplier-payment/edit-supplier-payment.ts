import { Component, inject, OnInit } from '@angular/core';
import { SupplierPaymentsForm } from '../../components/supplier-payments-form/supplier-payments-form';
import { ActivatedRoute, Router } from '@angular/router';
import { SupplierPaymentsService } from '../../services/supplier-payments.service';
import { SupplierPayment } from '../../models/supplier-payments.interface';
import { LanguageService } from '../../../../core/services/language.service';
import { finalize } from 'rxjs';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { SupplierPaymentsUpdateRequest } from '../../models/supplier-payments-update-request.interface';

@Component({
  selector: 'app-edit-supplier-payment',
  imports: [SupplierPaymentsForm],
  templateUrl: './edit-supplier-payment.html',
  styleUrl: './edit-supplier-payment.css',
})
export class EditSupplierPayment implements OnInit {
  public isLoading: boolean = false;
  public payment?: SupplierPayment;

  private id: number | null = null;
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private paymentsService = inject(SupplierPaymentsService);
  private languageService = inject(LanguageService);

  ngOnInit() {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) {
      this.router.navigate(['suppliers', 'payments']);
      return;
    }

    this.id = parseInt(tempId);
    this.fetchSupplierPayment(this.id);
  }

  public onSubmit(request: SupplierPaymentsUpdateRequest): void {
    this.isLoading = true;

    if (!this.id) {
      this.router.navigate(['suppliers', 'payments']);
      return;
    }

    this.paymentsService
      .updateSupplierPayment(this.id, request)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.languageService.toastSuccess('suppliers.messages.payment-success-update');
          this.router.navigate(['suppliers', 'payments']);
        },
        error: (err) => {
          const errorStatus = err.status;
          const errorCode = err.error?.error?.errorCode;

          if (errorStatus === 404) {
            this.languageService.toastError('suppliers.messages.not-found');
          } else if (errorStatus === 409 && errorCode === 'SUPPLIER_INACTIVE') {
            this.languageService.toastError('suppliers.messages.supplier-inactive');
          } else {
            this.languageService.toastError(handleHttpErrors(errorStatus));
          }
        },
      });
  }

  private fetchSupplierPayment(id: number): void {
    this.isLoading = true;

    this.paymentsService
      .fetchSupplierPaymentById(id)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          this.payment = res.data;
        },
        error: (err) => {
          const errorStatus = err.status;

          if (errorStatus === 404) {
            this.languageService.toastError('suppliers.messages.not-found');
            this.router.navigate(['suppliers', 'payments']);
          } else {
            this.languageService.toastError(handleHttpErrors(errorStatus));
          }
        },
      });
  }
}
