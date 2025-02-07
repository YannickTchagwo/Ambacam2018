import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {RouterTestingModule} from '@angular/router/testing';

import {AdminComponent} from './admin.component';
import {AdminHeaderComponent} from './components/page-elements/admin-header/admin-header.component';
import {AdminLeftSideComponent} from './components/page-elements/admin-left-side/admin-left-side.component';
import {AdminFooterComponent} from './components/page-elements/admin-footer/admin-footer.component';
import {AdminControlSidebarComponent} from './components/page-elements/admin-control-sidebar/admin-control-sidebar.component';

describe('AdminComponent', () => {
  let component: AdminComponent;
  let fixture: ComponentFixture<AdminComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        AdminComponent,
        AdminHeaderComponent,
        AdminLeftSideComponent,
        AdminFooterComponent,
        AdminControlSidebarComponent
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
