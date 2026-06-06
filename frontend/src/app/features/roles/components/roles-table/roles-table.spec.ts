import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RolesTable } from './roles-table';

describe('RolesTable', () => {
  let component: RolesTable;
  let fixture: ComponentFixture<RolesTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RolesTable]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RolesTable);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
