import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateShipment } from './create-shipment';

describe('CreateShipment', () => {
  let component: CreateShipment;
  let fixture: ComponentFixture<CreateShipment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateShipment]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateShipment);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
