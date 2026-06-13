import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SidebarDropdownMenu } from './sidebar-dropdown-menu';

describe('SidebarDropdownMenu', () => {
  let component: SidebarDropdownMenu;
  let fixture: ComponentFixture<SidebarDropdownMenu>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SidebarDropdownMenu]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SidebarDropdownMenu);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
