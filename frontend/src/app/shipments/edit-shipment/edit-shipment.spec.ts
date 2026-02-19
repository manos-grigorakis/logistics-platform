import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditShipment } from './edit-shipment';

describe('EditShipment', () => {
  let component: EditShipment;
  let fixture: ComponentFixture<EditShipment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditShipment]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditShipment);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
