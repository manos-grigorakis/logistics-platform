import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierPaymentsPage } from './supplier-payments-page';

describe('SupplierPaymentsPage', () => {
  let component: SupplierPaymentsPage;
  let fixture: ComponentFixture<SupplierPaymentsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupplierPaymentsPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupplierPaymentsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
