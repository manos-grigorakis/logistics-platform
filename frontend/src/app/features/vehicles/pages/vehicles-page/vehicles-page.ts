import { Component, inject, OnInit } from '@angular/core';
import { VehiclesService } from '../../vehicles.service';
import { VehicleResponse } from '../../models/vehicle-response';
import { VehiclesTable } from '../../components/vehicles-table/vehicles-table';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { RoundedIconButton } from '../../../../shared/components/forms/rounded-icon-button/rounded-icon-button';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../../../core/services/language.service';

@Component({
  selector: 'app-vehicles-page',
  imports: [VehiclesTable, LoadingSpinner, RoundedIconButton, RouterLink, TranslatePipe],
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
  private languageService = inject(LanguageService);

  ngOnInit(): void {
    this.fetchVehicles();
  }

  public onDeleteVehicleClick(id: number): void {
    this.isVehicleDeleted = true;

    this.vehiclesService.deleteVehicleById(id).subscribe({
      next: () => {
        this.isVehicleDeleted = false;
        this.vehicles = this.vehicles?.filter((v) => v.id !== id);
        this.languageService.toastSuccess('vehicles.messages.success-deletion');
      },
      error: (err) => {
        this.isVehicleDeleted = false;

        if (err.status === 404) {
          this.languageService.toastError('vehicles.messages.not-found');
        } else if (err.status === 500) {
          this.languageService.toastError('common.errors.server');
        } else {
          this.languageService.toastError('common.errors.generic');
        }
      },
    });
  }

  public onRefreshVehicleClick(): void {
    this.fetchVehicles();
  }

  private fetchVehicles(): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.vehiclesService.fetchAllVehicles().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.vehicles = res.data;
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
}
