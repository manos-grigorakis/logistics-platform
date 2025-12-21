import { Component, inject } from '@angular/core';
import { VehicleForm } from '../vehicle-form/vehicle-form';
import { VehicleRequest } from '../models/vehicle-request';
import { VehiclesService } from '../vehicles.service';
import { Router } from '@angular/router';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-create-vehicle',
  imports: [VehicleForm],
  templateUrl: './create-vehicle.html',
  styleUrl: './create-vehicle.css',
})
export class CreateVehicle {
  public isLoading: boolean = false;
  public errorMessage?: string;

  private vehiclesService = inject(VehiclesService);
  private router = inject(Router);

  public onSubmit(payload: VehicleRequest): void {
    this.isLoading = true;

    this.vehiclesService.createVehicle(payload).subscribe({
      next: () => {
        this.isLoading = false;
        toast.success('Vehicle created successfully');
        this.router.navigate(['vehicles']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 409) {
          this.errorMessage = `Vehicle already exists with plate number: ${payload.plate}`;
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }
      },
    });
  }
}
