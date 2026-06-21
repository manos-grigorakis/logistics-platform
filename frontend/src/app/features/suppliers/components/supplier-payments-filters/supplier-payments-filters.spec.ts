import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierPaymentsFilters } from './supplier-payments-filters';

describe('SupplierPaymentsFilters', () => {
  let component: SupplierPaymentsFilters;
  let fixture: ComponentFixture<SupplierPaymentsFilters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupplierPaymentsFilters]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupplierPaymentsFilters);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
