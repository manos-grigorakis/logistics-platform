import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewAddress } from './review-address';

describe('ReviewAddress', () => {
  let component: ReviewAddress;
  let fixture: ComponentFixture<ReviewAddress>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewAddress]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewAddress);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
