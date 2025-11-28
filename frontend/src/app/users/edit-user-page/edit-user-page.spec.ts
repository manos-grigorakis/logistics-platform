import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditUserPage } from './edit-user-page';

describe('EditUserPage', () => {
  let component: EditUserPage;
  let fixture: ComponentFixture<EditUserPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditUserPage],
    }).compileComponents();

    fixture = TestBed.createComponent(EditUserPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
