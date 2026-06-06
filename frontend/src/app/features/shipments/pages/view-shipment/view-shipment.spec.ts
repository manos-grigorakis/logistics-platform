import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewShipment } from './view-shipment';

describe('ViewShipment', () => {
  let component: ViewShipment;
  let fixture: ComponentFixture<ViewShipment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ViewShipment]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewShipment);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
