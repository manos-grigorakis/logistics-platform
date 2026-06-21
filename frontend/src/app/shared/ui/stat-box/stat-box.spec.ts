import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatBox } from './stat-box';

describe('StatBox', () => {
  let component: StatBox;
  let fixture: ComponentFixture<StatBox>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatBox]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StatBox);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
