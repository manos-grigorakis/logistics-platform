import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomersFilters } from './customers-filters';

describe('CustomersFilters', () => {
  let component: CustomersFilters;
  let fixture: ComponentFixture<CustomersFilters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomersFilters]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomersFilters);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
