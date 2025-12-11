import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuotesForm } from './quotes-form';

describe('QuotesForm', () => {
  let component: QuotesForm;
  let fixture: ComponentFixture<QuotesForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuotesForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuotesForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
