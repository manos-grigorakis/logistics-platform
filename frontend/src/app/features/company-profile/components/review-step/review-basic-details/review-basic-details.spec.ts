import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewBasicDetails } from './review-basic-details';

describe('ReviewBasicDetails', () => {
  let component: ReviewBasicDetails;
  let fixture: ComponentFixture<ReviewBasicDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewBasicDetails]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewBasicDetails);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
