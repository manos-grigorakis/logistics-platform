import { Component, inject, OnInit } from '@angular/core';
import { Supplier } from '../../models/supplier.interface';
import { SuppliersService } from '../../services/suppliers.service';
import { ActivatedRoute, Router } from '@angular/router';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { finalize } from 'rxjs';
import { SupplierRequest } from '../../models/supplier-request.interface';
import { LanguageService } from '../../../../core/services/language.service';
import { TranslatePipe } from '@ngx-translate/core';
import { SuppliersForm } from '../../components/suppliers-form/suppliers-form';

@Component({
  selector: 'app-edit-supplier',
  imports: [TranslatePipe, SuppliersForm],
  templateUrl: './edit-supplier.html',
  styleUrl: './edit-supplier.css',
})
export class EditSupplier implements OnInit {
  public isLoading: boolean = false;
  public errorMessage?: string;
  public supplier?: Supplier;

  // Routing
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  private id: number | null = null;

  // Services
  private supplierService = inject(SuppliersService);
  private languageService = inject(LanguageService);

  ngOnInit(): void {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) {
      this.router.navigate(['suppliers']);
      return;
    }

    this.id = parseInt(tempId);
    this.fetchSupplierById(this.id);
  }

  public onSubmitClick(data: SupplierRequest): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    if (!this.id) {
      this.router.navigate(['suppliers']);
      return;
    }

    this.supplierService
      .updateSupplierById(this.id, data)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.languageService.toastSuccess('suppliers.messages.success-update');
          this.router.navigate(['suppliers']);
        },
        error: (err) => {
          if (err.status === 404) {
            this.errorMessage = 'suppliers.messages.not-found';
          } else if (
            err.status === 409 &&
            err.error?.error?.details?.duplicateValue === 'COMPANY_NAME'
          ) {
            this.errorMessage = 'suppliers.messages.exists-by-company-name';
          } else {
            this.errorMessage = handleHttpErrors(err.status);
          }
        },
      });
  }

  private fetchSupplierById(id: number): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.supplierService
      .fetchSupplierById(id)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => (this.supplier = res.data),
        error: (err) => {
          if (err.status === 404) {
            this.errorMessage = 'suppliers.messages.not-found';
          } else {
            this.errorMessage = handleHttpErrors(err.status);
          }
        },
      });
  }
}
