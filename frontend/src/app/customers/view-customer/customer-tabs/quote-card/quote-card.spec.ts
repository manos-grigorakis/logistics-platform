import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuoteCard } from './quote-card';

describe('QuoteCard', () => {
  let component: QuoteCard;
  let fixture: ComponentFixture<QuoteCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuoteCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuoteCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
