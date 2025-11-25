import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainInput } from './main-input';

describe('MainInput', () => {
  let component: MainInput;
  let fixture: ComponentFixture<MainInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MainInput]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MainInput);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
