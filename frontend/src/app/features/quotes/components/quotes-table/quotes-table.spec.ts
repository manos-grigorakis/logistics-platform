import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuotesTable } from './quotes-table';

describe('QuotesTable', () => {
  let component: QuotesTable;
  let fixture: ComponentFixture<QuotesTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuotesTable]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuotesTable);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
