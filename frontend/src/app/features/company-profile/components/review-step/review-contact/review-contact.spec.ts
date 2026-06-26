import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewContact } from './review-contact';

describe('ReviewContact', () => {
  let component: ReviewContact;
  let fixture: ComponentFixture<ReviewContact>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewContact]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewContact);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
