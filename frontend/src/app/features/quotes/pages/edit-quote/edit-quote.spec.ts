import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditQuote } from './edit-quote';

describe('EditQuote', () => {
  let component: EditQuote;
  let fixture: ComponentFixture<EditQuote>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditQuote]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditQuote);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
