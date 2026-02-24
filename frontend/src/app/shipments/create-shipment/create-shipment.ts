import { Component, inject } from '@angular/core';
import { ShipmentsForm } from '../shipments-form/shipments-form';
import { ShipmentPayload } from '../models/shipment-payload';
import { ShipmentsService } from '../shipments.service';
import { Router } from '@angular/router';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-create-shipment',
  imports: [ShipmentsForm],
  templateUrl: './create-shipment.html',
  styleUrl: './create-shipment.css',
})
export class CreateShipment {
  private shipmentsService = inject(ShipmentsService);

  public router = inject(Router);
  public errorMessage?: string = undefined;

  public onSubmit(payload: ShipmentPayload): void {
    this.errorMessage = undefined;

    this.shipmentsService.createShipment(payload).subscribe({
      next: (res) => {
        toast.success('Shipment created successfully');
        this.router.navigate(['shipments']);
      },
      error: (err) => {
        if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occurred. Please try again';
        }
      },
    });
  }
}
