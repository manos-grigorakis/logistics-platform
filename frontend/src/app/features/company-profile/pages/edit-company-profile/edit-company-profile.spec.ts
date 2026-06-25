import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCompanyProfile } from './edit-company-profile';

describe('EditCompanyProfile', () => {
  let component: EditCompanyProfile;
  let fixture: ComponentFixture<EditCompanyProfile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditCompanyProfile]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditCompanyProfile);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
