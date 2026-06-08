import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ShipmentsService } from '../../shipments.service';
import { Shipment } from '../../models/shipment';
import { DatePipe, NgClass, DecimalPipe } from '@angular/common';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { shipmentStatusBadgeColor } from '../../utils/shipment-status-badge-color.utils';
import { ErrorAlert } from '../../../../shared/ui/error-alert/error-alert';
import { finalize } from 'rxjs';
import { CmrDocumentSummaryResponse } from '../../models/cmr-document-summary-response';
import { ModalFile } from '../../../../shared/ui/modal-file/modal-file';
import { RoundedIconButton } from '../../../../shared/components/forms/rounded-icon-button/rounded-icon-button';
import { QuotesService } from '../../../quotes/quotes.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-view-shipment',
  imports: [
    LoadingSpinner,
    DatePipe,
    NgClass,
    ErrorAlert,
    DecimalPipe,
    ModalFile,
    RoundedIconButton,
    TranslatePipe,
  ],
  templateUrl: './view-shipment.html',
  styleUrl: './view-shipment.css',
})
export class ViewShipment implements OnInit {
  private route = inject(ActivatedRoute);
  private shipmentsService = inject(ShipmentsService);
  private quoteService = inject(QuotesService);

  public shipment?: Shipment;
  public isLoading: boolean = false;
  public errorMessage?: string = undefined;

  // CMR Document
  public cmrDocument?: CmrDocumentSummaryResponse;
  public showCmrModal: boolean = false;
  public cmrDocumentUrl!: string;
  public cmrPdfName!: string;

  // Quote
  public showQuoteModal: boolean = false;
  public quoteUrl!: string;
  public quotePdfName!: string;

  ngOnInit(): void {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) return;
    this.fetchShipment(parseInt(tempId));
    this.fetchCmrDocumentsByShipment(parseInt(tempId));
  }

  public shipmentStatusBadgeColor(status: string): string {
    return shipmentStatusBadgeColor(status);
  }

  public cmrDocumentStatusBadgeColor(status: string): string {
    switch (status) {
      case 'generated':
        return 'bg-success-light text-success-dark';
      case 'signed':
        return 'bg-primary-100 text-primary-800';
      case 'cancelled':
        return 'bg-warning-light text-warning-dark';
      default:
        return 'bg-slate-200 text-slate-700';
    }
  }

  // Modals
  public openCmrDocument(): void {
    this.showCmrModal = true;
  }

  public closeCmrDocument(): void {
    this.showCmrModal = false;
  }

  public openQuoteModal(): void {
    this.showQuoteModal = true;
  }

  public closeQuoteModal(): void {
    this.showQuoteModal = false;
  }

  public showSignedText(isSigned: boolean): string {
    return isSigned ? 'common.messages.yes' : 'common.messages.no';
  }

  private fetchShipment(id: number): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.shipmentsService.getShipment(id).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.shipment = res.data;

        this.fetchQuoteById(this.shipment.quote.id);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 404) {
          this.errorMessage = 'shipments.messages.not-found';
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }

  private fetchCmrDocumentsByShipment(shipmentId: number): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.shipmentsService
      .getCmrDocumentByShipmentId(shipmentId)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          this.cmrDocument = res.data;
          this.cmrDocumentUrl = this.cmrDocument.fileUrl;
          this.cmrPdfName = this.cmrDocument.number;
        },
        error: (err) => {
          if (err.status === 500) {
            this.errorMessage = 'common.errors.server';
          }
        },
      });
  }

  private fetchQuoteById(id: number): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.quoteService
      .fetchQuoteById(id)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          this.quoteUrl = res.data.pdfUrl;
          this.quotePdfName = res.data.number;
        },
        error: (err) => {
          if (err.status === 500) {
            this.errorMessage = 'common.errors.server';
          }
        },
      });
  }
}
