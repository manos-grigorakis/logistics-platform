import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupPasswordPage } from './setup-password-page';

describe('SetupPasswordPage', () => {
  let component: SetupPasswordPage;
  let fixture: ComponentFixture<SetupPasswordPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetupPasswordPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SetupPasswordPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
