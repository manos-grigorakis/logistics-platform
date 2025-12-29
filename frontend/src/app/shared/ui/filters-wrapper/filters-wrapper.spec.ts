import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FiltersWrapper } from './filters-wrapper';

describe('FiltersWrapper', () => {
  let component: FiltersWrapper;
  let fixture: ComponentFixture<FiltersWrapper>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FiltersWrapper]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FiltersWrapper);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
