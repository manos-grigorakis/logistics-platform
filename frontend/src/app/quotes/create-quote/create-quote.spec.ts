import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateQuote } from './create-quote';

describe('CreateQuote', () => {
  let component: CreateQuote;
  let fixture: ComponentFixture<CreateQuote>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateQuote]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateQuote);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
