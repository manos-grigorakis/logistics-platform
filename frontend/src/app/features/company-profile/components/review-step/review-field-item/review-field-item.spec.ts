import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewFieldItem } from './review-field-item';

describe('ReviewFieldItem', () => {
  let component: ReviewFieldItem;
  let fixture: ComponentFixture<ReviewFieldItem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewFieldItem]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewFieldItem);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
