import { Component, inject, OnInit } from '@angular/core';
import { ShipmentsForm } from '../shipments-form/shipments-form';
import { ShipmentsService } from '../shipments.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Shipment } from '../models/shipment';
import { toast } from 'ngx-sonner';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';

@Component({
  selector: 'app-edit-shipment',
  imports: [ShipmentsForm, LoadingSpinner],
  templateUrl: './edit-shipment.html',
  styleUrl: './edit-shipment.css',
})
export class EditShipment implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private id: number = 0;
  private shipmentsService = inject(ShipmentsService);

  public isLoading: boolean = false;
  public errorMessage?: string = undefined;
  public shipment?: Shipment;

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
      next: (res) => {
        this.isLoading = false;
        toast.success('Shipment updated successfully');
        this.router.navigate(['/shipments']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again later';
        } else {
          this.errorMessage = 'An error occured. Please try again';
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
        this.shipment = res;
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 404) {
          this.errorMessage = 'Shipment not found';
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error has occured. Please try again';
        }
      },
    });
  }
}
