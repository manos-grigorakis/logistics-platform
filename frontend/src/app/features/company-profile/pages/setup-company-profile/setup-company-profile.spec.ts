import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupCompanyProfile } from './setup-company-profile';

describe('SetupCompanyProfile', () => {
  let component: SetupCompanyProfile;
  let fixture: ComponentFixture<SetupCompanyProfile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetupCompanyProfile]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SetupCompanyProfile);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
