import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehiclesTable } from './vehicles-table';

describe('VehiclesTable', () => {
  let component: VehiclesTable;
  let fixture: ComponentFixture<VehiclesTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehiclesTable]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VehiclesTable);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
