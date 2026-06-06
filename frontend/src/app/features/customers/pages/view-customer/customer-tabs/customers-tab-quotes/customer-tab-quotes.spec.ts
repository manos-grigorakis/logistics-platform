import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerTabQuotes } from './customer-tab-quotes';

describe('CustomerTabQuotes', () => {
  let component: CustomerTabQuotes;
  let fixture: ComponentFixture<CustomerTabQuotes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerTabQuotes]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerTabQuotes);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
