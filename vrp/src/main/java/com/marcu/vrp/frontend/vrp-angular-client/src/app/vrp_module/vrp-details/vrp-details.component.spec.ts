import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VrpDetailsComponent } from './vrp-details.component';

describe('VrpDetailsComponent', () => {
  let component: VrpDetailsComponent;
  let fixture: ComponentFixture<VrpDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VrpDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VrpDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
