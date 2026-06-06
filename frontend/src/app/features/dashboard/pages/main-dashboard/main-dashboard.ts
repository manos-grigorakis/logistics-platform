import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { KpiCard } from '../../components/kpi-card/kpi-card';
import { CurrencyPipe } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartData, ChartOptions } from 'chart.js';
import { AnalyticsService } from '../../analytics.service';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../../../core/services/language.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-main-dashboard',
  imports: [KpiCard, CurrencyPipe, BaseChartDirective, TranslatePipe],
  templateUrl: './main-dashboard.html',
  styleUrl: './main-dashboard.css',
})
export class MainDashboard implements OnInit, OnDestroy {
  private analyticsService = inject(AnalyticsService);
  private languageService = inject(LanguageService);

  public totalCustomers: number = 0;
  public totalShipments: number = 0;
  public totalOutstandingAmount: number = 0;
  public totalPendingShipments: number = 0;

  public quotesByStatusChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: this.languageService.translateKey('dashboard.quotes-label'),
      },
    ],
  };

  public shipmentsByStatusChartData: ChartData<'pie'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: this.languageService.translateKey('dashboard.shipments-label'),
      },
    ],
  };

  public invoicesByStatusChartData: ChartData<'pie'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: this.languageService.translateKey('dashboard.invoices-label'),
      },
    ],
  };

  public pieChartOptions: ChartOptions<'pie'> = {
    plugins: { legend: { position: 'bottom' } },
  };

  private langChangeSub?: Subscription;

  ngOnInit(): void {
    this.loadAll();

    this.langChangeSub = this.languageService.onLangChange.subscribe(() => this.loadAll());
  }

  ngOnDestroy(): void {
    this.langChangeSub?.unsubscribe();
  }

  /**
   * Fetches all the analytics
   */
  private loadAll(): void {
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
          labels: res.data.map((item) =>
            this.languageService.translateKey(
              `metadata.quotes-statuses.${item.status.toLowerCase()}`,
            ),
          ),
          datasets: [
            {
              data: res.data.map((item) => item.count),
              label: this.languageService.translateKey('dashboard.quotes-label'),
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
          labels: res.data.map((item) =>
            this.languageService.translateKey(
              `metadata.shipments-statuses.${item.status.toLowerCase()}`,
            ),
          ),
          datasets: [
            {
              data: res.data.map((item) => item.count),
              label: this.languageService.translateKey('dashboard.shipments-label'),
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
          labels: res.data.map((item) =>
            this.languageService.translateKey(
              `metadata.invoices-statuses.${item.status.toLowerCase()}`,
            ),
          ),
          datasets: [
            {
              data: res.data.map((item) => item.count),
              label: this.languageService.translateKey('dashboard.invoices-label'),
            },
          ],
        };
      },
      error: (error) => console.error(error),
    });
  }
}
