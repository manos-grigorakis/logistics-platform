import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditSupplierPayment } from './edit-supplier-payment';

describe('EditSupplierPayment', () => {
  let component: EditSupplierPayment;
  let fixture: ComponentFixture<EditSupplierPayment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditSupplierPayment]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditSupplierPayment);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
