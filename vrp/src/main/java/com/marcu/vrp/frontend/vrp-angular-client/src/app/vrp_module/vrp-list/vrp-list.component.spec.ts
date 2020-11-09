import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VrpListComponent } from './vrp-list.component';

describe('VrpListComponent', () => {
  let component: VrpListComponent;
  let fixture: ComponentFixture<VrpListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VrpListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VrpListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
