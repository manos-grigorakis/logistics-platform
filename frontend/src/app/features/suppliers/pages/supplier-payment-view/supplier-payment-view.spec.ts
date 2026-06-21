import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierPaymentView } from './supplier-payment-view';

describe('SupplierPaymentView', () => {
  let component: SupplierPaymentView;
  let fixture: ComponentFixture<SupplierPaymentView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupplierPaymentView]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupplierPaymentView);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
