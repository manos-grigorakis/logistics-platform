import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CmrDocumentsPage } from './cmr-documents-page';

describe('CmrDocumentsPage', () => {
  let component: CmrDocumentsPage;
  let fixture: ComponentFixture<CmrDocumentsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CmrDocumentsPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CmrDocumentsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
