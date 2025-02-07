import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RouterTestingModule } from '@angular/router/testing';

import { StarterComponent } from './history.component';
import { StarterHeaderComponent } from './components/page-elements/starter-header/starter-header.component';
import { StarterLeftSideComponent } from './components/page-elements/starter-left-side/starter-left-side.component';
import { StarterContentComponent } from './starter-content/starter-content.component';
import { StarterFooterComponent } from './components/page-elements/starter-footer/starter-footer.component';
import { StarterControlSidebarComponent } from './components/page-elements/starter-control-sidebar/starter-control-sidebar.component';

describe('StarterComponent', () => {
  let component: StarterComponent;
  let fixture: ComponentFixture<StarterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        StarterComponent,
        StarterHeaderComponent,
        StarterLeftSideComponent,
        StarterContentComponent,
        StarterFooterComponent,
        StarterControlSidebarComponent
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StarterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should render titleConfirmDeleteDialog in a h1 tag', async(() => {
    const fixture = TestBed.createComponent(StarterComponent);
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain('Page Header');
  }));
});
