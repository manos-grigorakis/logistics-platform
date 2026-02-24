import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShipmentsFilters } from './shipments-filters';

describe('ShipmentsFilters', () => {
  let component: ShipmentsFilters;
  let fixture: ComponentFixture<ShipmentsFilters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShipmentsFilters]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShipmentsFilters);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
