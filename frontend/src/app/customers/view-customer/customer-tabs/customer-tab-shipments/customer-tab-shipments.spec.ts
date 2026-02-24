import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerTabShipments } from './customer-tab-shipments';

describe('CustomerTabShipments', () => {
  let component: CustomerTabShipments;
  let fixture: ComponentFixture<CustomerTabShipments>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerTabShipments]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerTabShipments);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
