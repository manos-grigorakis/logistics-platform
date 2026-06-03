import { Component, inject, OnInit } from '@angular/core';
import { KpiCard } from './kpi-card/kpi-card';
import { CurrencyPipe } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartData, ChartOptions } from 'chart.js';
import { formatEnumLabel } from '../../shared/utils/format-enum-label.util';
import { AnalyticsService } from '../analytics.service';

@Component({
  selector: 'app-main-dashboard',
  imports: [KpiCard, CurrencyPipe, BaseChartDirective],
  templateUrl: './main-dashboard.html',
  styleUrl: './main-dashboard.css',
})
export class MainDashboard implements OnInit {
  private analyticsService = inject(AnalyticsService);

  public totalCustomers: number = 0;
  public totalShipments: number = 0;
  public totalOutstandingAmount: number = 0;
  public totalPendingShipments: number = 0;

  public quotesByStatusChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Quotes',
      },
    ],
  };

  public shipmentsByStatusChartData: ChartData<'pie'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Shipments',
      },
    ],
  };

  public invoicesByStatusChartData: ChartData<'pie'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Invoices',
      },
    ],
  };

  public pieChartOptions: ChartOptions<'pie'> = {
    plugins: { legend: { position: 'bottom' } },
  };

  ngOnInit(): void {
    this.loadTotalCustomers();
    this.loadTotalShipments();
    this.loadTotalOutstandingAmount();
    this.loadTotalPendingShipments();
    this.loadQuotesByStatus();
    this.loadShipmentsByStatus();
    this.loadInvoicesByStatus();
  }

  private loadTotalCustomers(): void {
    this.analyticsService.fetchTotalCustomers().subscribe({
      next: (res) => {
        this.totalCustomers = res.data.value;
      },
      error: (error) => console.error(error),
    });
  }

  private loadTotalShipments(): void {
    this.analyticsService.fetchTotalShipments().subscribe({
      next: (res) => {
        this.totalShipments = res.data.value;
      },
      error: (error) => console.error(error),
    });
  }

  private loadTotalOutstandingAmount(): void {
    this.analyticsService.fetchTotalOutstandingAmount().subscribe({
      next: (res) => {
        this.totalOutstandingAmount = res.data.value;
      },
      error: (error) => console.error(error),
    });
  }

  private loadTotalPendingShipments(): void {
    this.analyticsService.fetchTotalPendingShipments().subscribe({
      next: (res) => {
        this.totalPendingShipments = res.data.value;
      },
      error: (error) => console.error(error),
    });
  }

  private loadQuotesByStatus(): void {
    this.analyticsService.fetchQuotesByStatus().subscribe({
      next: (res) => {
        this.quotesByStatusChartData = {
          labels: res.data.map((item) => formatEnumLabel(item.status)),
          datasets: [
            {
              data: res.data.map((item) => item.count),
              label: 'Quotes',
            },
          ],
        };
      },
      error: (error) => console.error(error),
    });
  }

  private loadShipmentsByStatus(): void {
    this.analyticsService.fetchShipmentsByStatus().subscribe({
      next: (res) => {
        this.shipmentsByStatusChartData = {
          labels: res.data.map((item) => formatEnumLabel(item.status)),
          datasets: [
            {
              data: res.data.map((item) => item.count),
              label: 'Shipments',
            },
          ],
        };
      },
      error: (error) => console.error(error),
    });
  }

  private loadInvoicesByStatus(): void {
    this.analyticsService.fetchInvoicesByStatus().subscribe({
      next: (res) => {
        this.invoicesByStatusChartData = {
          labels: res.data.map((item) => formatEnumLabel(item.status)),
          datasets: [
            {
              data: res.data.map((item) => item.count),
              label: 'Invoices',
            },
          ],
        };
      },
      error: (error) => console.error(error),
    });
  }
}
