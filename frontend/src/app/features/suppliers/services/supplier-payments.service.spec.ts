/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { SupplierPaymentsService } from './supplier-payments.service';

describe('Service: SupplierPayments', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SupplierPaymentsService]
    });
  });

  it('should ...', inject([SupplierPaymentsService], (service: SupplierPaymentsService) => {
    expect(service).toBeTruthy();
  }));
});
