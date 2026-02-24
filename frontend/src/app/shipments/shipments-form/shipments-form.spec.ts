import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShipmentsForm } from './shipments-form';

describe('ShipmentsForm', () => {
  let component: ShipmentsForm;
  let fixture: ComponentFixture<ShipmentsForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShipmentsForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShipmentsForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
