import { Component, inject } from '@angular/core';
import { SuppliersService } from '../../services/suppliers.service';
import { LanguageService } from '../../../../core/services/language.service';
import { SupplierRequest } from '../../models/supplier-request.interface';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { finalize } from 'rxjs';
import { Router } from '@angular/router';
import { SuppliersForm } from '../../components/suppliers-form/suppliers-form';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-suppliers-create',
  imports: [SuppliersForm, TranslatePipe],
  templateUrl: './suppliers-create.html',
  styleUrl: './suppliers-create.css',
})
export class SuppliersCreate {
  public isLoading: boolean = false;
  public errorMessage?: string = undefined;

  private router = inject(Router);

  // Services
  private supplierService = inject(SuppliersService);
  private languageService = inject(LanguageService);

  public onSubmit(data: SupplierRequest): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.supplierService
      .createSupplier(data)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.languageService.toastSuccess('suppliers.messages.success-creation');
          this.router.navigate(['suppliers']);
        },
        error: (err) => {
          if (err.status === 409 && err.error?.error?.details?.duplicateValue === 'COMPANY_NAME') {
            this.errorMessage = 'suppliers.messages.exists-by-company-name';
          } else {
            this.errorMessage = handleHttpErrors(err.status);
          }
        },
      });
  }
}
