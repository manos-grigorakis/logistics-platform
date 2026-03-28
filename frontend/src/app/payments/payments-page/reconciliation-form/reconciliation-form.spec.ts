import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReconciliationForm } from './reconciliation-form';

describe('ReconciliationForm', () => {
  let component: ReconciliationForm;
  let fixture: ComponentFixture<ReconciliationForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReconciliationForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReconciliationForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
