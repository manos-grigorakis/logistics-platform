import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierPaymentsForm } from './supplier-payments-form';

describe('SupplierPaymentsForm', () => {
  let component: SupplierPaymentsForm;
  let fixture: ComponentFixture<SupplierPaymentsForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupplierPaymentsForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupplierPaymentsForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
