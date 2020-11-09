import { TestBed } from '@angular/core/testing';

import { VrpService } from './vrp.service';

describe('VrpService', () => {
  let service: VrpService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VrpService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
