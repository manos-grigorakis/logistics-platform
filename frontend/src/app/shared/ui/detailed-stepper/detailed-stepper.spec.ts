import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailedStepper } from './detailed-stepper';

describe('DetailedStepper', () => {
  let component: DetailedStepper;
  let fixture: ComponentFixture<DetailedStepper>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailedStepper]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailedStepper);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
