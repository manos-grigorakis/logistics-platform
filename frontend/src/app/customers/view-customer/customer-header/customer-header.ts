import { Component, Input } from '@angular/core';
import { Customer } from '../../models/customer';
import { NgClass } from '@angular/common';
import { customerTypeBadgeColor } from '../../utils/customer-type-color.utils';

@Component({
  selector: 'app-customer-header',
  imports: [NgClass],
  templateUrl: './customer-header.html',
  styleUrl: './customer-header.css',
})
export class CustomerHeader {
  @Input() customer?: Customer;

  public customerTypeBadgeColor(type: string): string {
    return customerTypeBadgeColor(type);
  }
}
