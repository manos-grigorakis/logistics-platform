import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoundedIconButton } from './rounded-icon-button';

describe('RoundedIconButton', () => {
  let component: RoundedIconButton;
  let fixture: ComponentFixture<RoundedIconButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoundedIconButton]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoundedIconButton);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
