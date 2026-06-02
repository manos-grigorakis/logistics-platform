import { Component, inject, OnInit } from '@angular/core';
import { KpiCard } from './kpi-card/kpi-card';
import { AnalyticsService } from './analytics.service';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-main-dashboard',
  imports: [KpiCard, CurrencyPipe],
  templateUrl: './main-dashboard.html',
  styleUrl: './main-dashboard.css',
})
export class MainDashboard implements OnInit {
  private analyticsService = inject(AnalyticsService);

  public totalCustomers: number = 0;
  public totalShipments: number = 0;
  public totalOutstandingAmount: number = 0;
  public totalPendingShipments: number = 0;

  ngOnInit(): void {
    this.loadTotalCustomers();
    this.loadTotalShipments();
    this.loadTotalOutstandingAmount();
    this.loadTotalPendingShipments();
  }

  private loadTotalCustomers(): void {
    this.analyticsService.fetchTotalCustomers().subscribe({
      next: (response) => {
        this.totalCustomers = response.value;
      },
      error: (error) => console.error(error),
    });
  }

  private loadTotalShipments(): void {
    this.analyticsService.fetchTotalShipments().subscribe({
      next: (response) => {
        this.totalShipments = response.value;
      },
      error: (error) => console.error(error),
    });
  }

  private loadTotalOutstandingAmount(): void {
    this.analyticsService.fetchTotalOutstandingAmount().subscribe({
      next: (response) => {
        this.totalOutstandingAmount = response.value;
      },
      error: (error) => console.error(error),
    });
  }

  private loadTotalPendingShipments(): void {
    this.analyticsService.fetchTotalPendingShipments().subscribe({
      next: (response) => {
        this.totalPendingShipments = response.value;
      },
      error: (error) => console.error(error),
    });
  }
}
