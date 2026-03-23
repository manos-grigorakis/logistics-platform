import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { FileDropzone } from '../../shared/ui/file-dropzone/file-dropzone';
import { CustomersService } from '../../customers/customers.service';
import { Customer } from '../../customers/models/customer';
import { NgSelectComponent } from '@ng-select/ng-select';
import { Subject } from 'rxjs';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { NgIcon } from '@ng-icons/core';
import { DetailedStepper } from '../../shared/ui/detailed-stepper/detailed-stepper';

@Component({
  selector: 'app-payments-page',
  imports: [FileDropzone, NgSelectComponent, PrimaryButton, NgIcon, DetailedStepper],
  templateUrl: './payments-page.html',
  styleUrl: './payments-page.css',
})
export class PaymentsPage implements OnInit {
  // Stepper
  public activeStep: number = 0;
  public stepperData: { title: string; description: string }[] = [
    { title: 'Select Customer & Upload', description: 'Choose a customer and upload files' },
    { title: 'Processing', description: 'Matching Transactions' },
    { title: 'Results', description: 'Download results' },
  ];

  // Customer
  @ViewChild('customerSelect') customerSelect!: any;
  public customersList: Customer[] = [];
  public customersLoading: boolean = false;
  public customersErrorMessage?: string = undefined;
  public customerSearch$!: Subject<string>;

  private customersService = inject(CustomersService);

  ngOnInit(): void {
    this.fetchCustomers('');
  }

  public onClickFocusCustomersSelect(): void {
    this.customerSelect.focus();
  }

  private fetchCustomers(customer: string): void {
    this.customersLoading = true;
    this.customersErrorMessage = undefined;

    this.customersService.fetchCustomers({ companyName: customer }).subscribe({
      next: (res) => {
        this.customersLoading = false;
        this.customersList = res.content;
      },
      error: (err) => {
        this.customersLoading = false;
        this.customersErrorMessage = 'Failed to fetch Customers';
      },
    });
  }
}
