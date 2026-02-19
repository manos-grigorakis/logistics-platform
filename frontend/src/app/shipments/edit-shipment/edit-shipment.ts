import { Component } from '@angular/core';
import { ShipmentsForm } from '../shipments-form/shipments-form';

@Component({
  selector: 'app-edit-shipment',
  imports: [ShipmentsForm],
  templateUrl: './edit-shipment.html',
  styleUrl: './edit-shipment.css',
})
export class EditShipment {}
