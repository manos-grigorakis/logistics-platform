import { Component, inject } from '@angular/core';
import { SupplierPaymentsForm } from '../../components/supplier-payments-form/supplier-payments-form';
import { SupplierPaymentsService } from '../../services/supplier-payments.service';
import { SupplierPaymentsCreateRequest } from '../../models/supplier-payments-create-request.interface';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { LanguageService } from '../../../../core/services/language.service';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';

@Component({
  selector: 'app-supplier-payments-create',
  imports: [SupplierPaymentsForm],
  templateUrl: './supplier-payments-create.html',
  styleUrl: './supplier-payments-create.css',
})
export class SupplierPaymentsCreate {
  public isLoading: boolean = false;

  private router = inject(Router);
  private paymentsService = inject(SupplierPaymentsService);
  private languageService = inject(LanguageService);

  public onSubmit(request: SupplierPaymentsCreateRequest): void {
    this.isLoading = true;

    this.paymentsService
      .createSupplierPayment(request)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.languageService.toastSuccess('suppliers.messages.payment-success-creation');
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
}
