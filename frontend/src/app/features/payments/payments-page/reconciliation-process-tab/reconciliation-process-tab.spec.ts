import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReconciliationProcessTab } from './reconciliation-process-tab';

describe('ReconciliationProcessTab', () => {
  let component: ReconciliationProcessTab;
  let fixture: ComponentFixture<ReconciliationProcessTab>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReconciliationProcessTab]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReconciliationProcessTab);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
