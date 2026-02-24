import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShipmentsCard } from './shipments-card';

describe('ShipmentsCard', () => {
  let component: ShipmentsCard;
  let fixture: ComponentFixture<ShipmentsCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShipmentsCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShipmentsCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
