import { Component, inject, OnInit } from '@angular/core';
import { VehicleForm } from '../vehicle-form/vehicle-form';
import { VehiclesService } from '../vehicles.service';
import { ActivatedRoute, Router } from '@angular/router';
import { VehicleResponse } from '../models/vehicle-response';
import { toast } from 'ngx-sonner';
import { VehicleRequest } from '../models/vehicle-request';

@Component({
  selector: 'app-edit-vehicle',
  imports: [VehicleForm],
  templateUrl: './edit-vehicle.html',
  styleUrl: './edit-vehicle.css',
})
export class EditVehicle implements OnInit {
  public isLoading: boolean = false;
  public vehicle?: VehicleResponse;
  public errorMessage?: string;

  private vehiclesService = inject(VehiclesService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private id?: number;

  ngOnInit(): void {
    let id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      toast.error('Vehicle doesnt exist');
      this.router.navigate(['vehicles']);
      return;
    }

    this.id = parseInt(id);
    this.fetchSelectedVehicle(this.id);
  }

  public onSubmitForm(payload: VehicleRequest): void {
    if (!this.id) return;

    this.isLoading = true;

    this.vehiclesService.updateVehicle(this.id, payload).subscribe({
      next: () => {
        this.isLoading = false;
        toast.success('Vehicle updated successfully');
        this.router.navigate(['vehicles']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 404) {
          this.errorMessage = "Vehicle doesn't exists";
        } else if (err.status === 409) {
          this.errorMessage = `Vehicle already exists with plate number: ${payload.plate}`;
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }
      },
    });
  }

  private fetchSelectedVehicle(id: number): void {
    this.vehiclesService.fetchVehicleById(id).subscribe({
      next: (res) => {
        this.vehicle = res;
      },
      error: (err) => {
        if (err.status === 404) {
          toast.error('Vehicle doesnt exist');
          this.router.navigate(['vehicles']);
        } else if (err.status === 500) {
          toast.error('Server error. Please try again');
        } else {
          toast.error('An error occurred. Please try again');
        }
      },
    });
  }
}
