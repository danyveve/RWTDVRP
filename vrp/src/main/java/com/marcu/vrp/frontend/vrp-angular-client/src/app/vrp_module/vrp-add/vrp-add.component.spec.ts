import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VrpAddComponent } from './vrp-add.component';

describe('VrpAddComponent', () => {
  let component: VrpAddComponent;
  let fixture: ComponentFixture<VrpAddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VrpAddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VrpAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
