import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CmrDocumentsFilters } from './cmr-documents-filters';

describe('CmrDocumentsFilters', () => {
  let component: CmrDocumentsFilters;
  let fixture: ComponentFixture<CmrDocumentsFilters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CmrDocumentsFilters]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CmrDocumentsFilters);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
