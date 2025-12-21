import { Component, inject, OnInit } from '@angular/core';
import { VehiclesService } from '../vehicles.service';
import { VehicleResponse } from '../models/vehicle-response';
import { VehiclesTable } from '../vehicles-table/vehicles-table';
import { toast } from 'ngx-sonner';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';

@Component({
  selector: 'app-vehicles-page',
  imports: [VehiclesTable, LoadingSpinner],
  templateUrl: './vehicles-page.html',
  styleUrl: './vehicles-page.css',
})
export class VehiclesPage implements OnInit {
  // UI
  public isLoading: boolean = false;
  public errorMessage?: string = undefined;
  public isVehicleDeleted: boolean = false;

  // Vehicles
  public vehicles?: VehicleResponse[];

  // Services
  private vehiclesService = inject(VehiclesService);

  ngOnInit(): void {
    this.fetchVehicles();
  }

  public onDeleteVehicleClick(id: number): void {
    this.isVehicleDeleted = true;

    this.vehiclesService.deleteVehicleById(id).subscribe({
      next: () => {
        this.isVehicleDeleted = false;
        this.vehicles = this.vehicles?.filter((v) => v.id !== id);
        toast.success('Vehicle deleted successfully');
      },
      error: (err) => {
        this.isVehicleDeleted = false;

        if (err.status === 404) {
          toast.error('Vehicle doesnt exist');
        } else if (err.status === 500) {
          toast.error('Server error. Please try again');
        } else {
          toast.error('An error occurred. Please try again');
        }
      },
    });
  }

  private fetchVehicles(): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.vehiclesService.fetchAllVehicles().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.vehicles = res;
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occurred. Please try again';
        }
      },
    });
  }
}
