import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasicDetails } from './basic-details';

describe('BasicDetails', () => {
  let component: BasicDetails;
  let fixture: ComponentFixture<BasicDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BasicDetails],
    }).compileComponents();

    fixture = TestBed.createComponent(BasicDetails);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
