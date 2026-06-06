import { Component, inject, OnInit } from '@angular/core';
import { ShipmentsForm } from '../shipments-form/shipments-form';
import { ShipmentsService } from '../shipments.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Shipment } from '../models/shipment';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { LanguageService } from '../../shared/services/language.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-edit-shipment',
  imports: [ShipmentsForm, LoadingSpinner, TranslatePipe],
  templateUrl: './edit-shipment.html',
  styleUrl: './edit-shipment.css',
})
export class EditShipment implements OnInit {
  public shipment?: Shipment;
  public isLoading: boolean = false;
  public errorMessage?: string = undefined;

  private id: number = 0;
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private shipmentsService = inject(ShipmentsService);
  private languageService = inject(LanguageService);

  ngOnInit(): void {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) return;
    this.id = parseInt(tempId);
    this.fetchShipment(this.id);
  }

  public onSubmit(payload: any): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.shipmentsService.updateShipment(this.id, payload).subscribe({
      next: () => {
        this.isLoading = false;
        this.languageService.toastSuccess('shipments.messages.success-update');
        this.router.navigate(['/shipments']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }

  private fetchShipment(id: number): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.shipmentsService.getShipment(id).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.shipment = res.data;
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 404) {
          this.errorMessage = 'shipments.messages.not-found';
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }
}
