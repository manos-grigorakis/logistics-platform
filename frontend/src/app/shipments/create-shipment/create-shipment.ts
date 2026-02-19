import { Component, inject } from '@angular/core';
import { ShipmentsForm } from '../shipments-form/shipments-form';
import { ShipmentPayload } from '../models/shipment-payload';
import { ShipmentsService } from '../shipments.service';

@Component({
  selector: 'app-create-shipment',
  imports: [ShipmentsForm],
  templateUrl: './create-shipment.html',
  styleUrl: './create-shipment.css',
})
export class CreateShipment {
  private shipmentsService = inject(ShipmentsService);

  public onSubmit(payload: ShipmentPayload): void {
    this.shipmentsService.createShipment(payload).subscribe({
      next: (res) => {
        console.log('success');
      },
      error: (err) => {
        console.error(err);
      },
    });
  }
}
