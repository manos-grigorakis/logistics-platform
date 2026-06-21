import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierPaymentsCreate } from './supplier-payments-create';

describe('SupplierPaymentsCreate', () => {
  let component: SupplierPaymentsCreate;
  let fixture: ComponentFixture<SupplierPaymentsCreate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupplierPaymentsCreate]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupplierPaymentsCreate);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
