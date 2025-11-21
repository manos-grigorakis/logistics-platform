import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupPasswordForm } from './setup-password-form';

describe('SetupPasswordForm', () => {
  let component: SetupPasswordForm;
  let fixture: ComponentFixture<SetupPasswordForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetupPasswordForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SetupPasswordForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
