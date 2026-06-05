import { Component, Input } from '@angular/core';
import { Customer } from '../../models/customer';
import { NgIcon } from '@ng-icons/core';
import { DatePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-customer-sidebar',
  imports: [NgIcon, DatePipe, TranslatePipe],
  templateUrl: './customer-sidebar.html',
  styleUrl: './customer-sidebar.css',
})
export class CustomerSidebar {
  @Input() customer?: Customer;
}
