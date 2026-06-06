import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuotesFilters } from './quotes-filters';

describe('QuotesFilters', () => {
  let component: QuotesFilters;
  let fixture: ComponentFixture<QuotesFilters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuotesFilters]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuotesFilters);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
