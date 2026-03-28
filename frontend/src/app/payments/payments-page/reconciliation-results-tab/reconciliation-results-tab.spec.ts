import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReconciliationResultsTab } from './reconciliation-results-tab';

describe('ReconciliationResultsTab', () => {
  let component: ReconciliationResultsTab;
  let fixture: ComponentFixture<ReconciliationResultsTab>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReconciliationResultsTab],
    }).compileComponents();

    fixture = TestBed.createComponent(ReconciliationResultsTab);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
