import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierPaymentsTable } from './supplier-payments-table';

describe('SupplierPaymentsTable', () => {
  let component: SupplierPaymentsTable;
  let fixture: ComponentFixture<SupplierPaymentsTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupplierPaymentsTable]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupplierPaymentsTable);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
