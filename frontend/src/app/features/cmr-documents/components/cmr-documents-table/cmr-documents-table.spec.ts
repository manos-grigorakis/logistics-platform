import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CmrDocumentsTable } from './cmr-documents-table';

describe('CmrDocumentsTable', () => {
  let component: CmrDocumentsTable;
  let fixture: ComponentFixture<CmrDocumentsTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CmrDocumentsTable]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CmrDocumentsTable);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
