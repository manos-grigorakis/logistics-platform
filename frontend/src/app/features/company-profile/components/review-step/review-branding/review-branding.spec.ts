import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewBranding } from './review-branding';

describe('ReviewBranding', () => {
  let component: ReviewBranding;
  let fixture: ComponentFixture<ReviewBranding>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewBranding]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewBranding);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
