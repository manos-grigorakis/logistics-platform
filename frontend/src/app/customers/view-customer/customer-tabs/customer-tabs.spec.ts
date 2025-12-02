import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerTabs } from './customer-tabs';

describe('CustomerTabs', () => {
  let component: CustomerTabs;
  let fixture: ComponentFixture<CustomerTabs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerTabs]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerTabs);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
