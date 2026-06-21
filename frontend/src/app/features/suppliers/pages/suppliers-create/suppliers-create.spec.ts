import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuppliersCreate } from './suppliers-create';

describe('SuppliersCreate', () => {
  let component: SuppliersCreate;
  let fixture: ComponentFixture<SuppliersCreate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SuppliersCreate]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SuppliersCreate);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
