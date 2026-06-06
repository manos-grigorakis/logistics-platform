import { Component, inject } from '@angular/core';
import { ShipmentsForm } from '../shipments-form/shipments-form';
import { ShipmentPayload } from '../models/shipment-payload';
import { ShipmentsService } from '../shipments.service';
import { Router } from '@angular/router';
import { LanguageService } from '../../shared/services/language.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-create-shipment',
  imports: [ShipmentsForm, TranslatePipe],
  templateUrl: './create-shipment.html',
  styleUrl: './create-shipment.css',
})
export class CreateShipment {
  public router = inject(Router);
  public errorMessage?: string = undefined;

  private shipmentsService = inject(ShipmentsService);
  private languageService = inject(LanguageService);

  public onSubmit(payload: ShipmentPayload): void {
    this.errorMessage = undefined;

    this.shipmentsService.createShipment(payload).subscribe({
      next: () => {
        this.languageService.toastSuccess('shipments.messages.success-creation');
        this.router.navigate(['shipments']);
      },
      error: (err) => {
        if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }
}
