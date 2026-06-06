import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicesAndItems } from './services-and-items';

describe('ServicesAndItems', () => {
  let component: ServicesAndItems;
  let fixture: ComponentFixture<ServicesAndItems>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServicesAndItems]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServicesAndItems);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
