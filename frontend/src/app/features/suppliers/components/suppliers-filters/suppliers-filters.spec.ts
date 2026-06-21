import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuppliersFilters } from './suppliers-filters';

describe('SuppliersFilters', () => {
  let component: SuppliersFilters;
  let fixture: ComponentFixture<SuppliersFilters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SuppliersFilters]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SuppliersFilters);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
