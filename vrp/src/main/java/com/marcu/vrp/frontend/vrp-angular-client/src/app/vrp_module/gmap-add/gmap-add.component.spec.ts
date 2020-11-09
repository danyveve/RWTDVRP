import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GmapAddComponent } from './gmap-add.component';

describe('GmapAddComponent', () => {
  let component: GmapAddComponent;
  let fixture: ComponentFixture<GmapAddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GmapAddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GmapAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
