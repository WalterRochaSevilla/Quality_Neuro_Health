import { TestBed } from '@angular/core/testing';

import { EmocionService } from './emocion.service';

describe('EmocionService', () => {
  let service: EmocionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EmocionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
