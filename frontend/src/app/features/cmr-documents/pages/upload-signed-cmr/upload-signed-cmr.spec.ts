import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadSignedCmr } from './upload-signed-cmr';

describe('UploadSignedCmr', () => {
  let component: UploadSignedCmr;
  let fixture: ComponentFixture<UploadSignedCmr>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadSignedCmr]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UploadSignedCmr);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
