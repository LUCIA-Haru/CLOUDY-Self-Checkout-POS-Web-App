import { TestBed } from '@angular/core/testing';

import { ProductAllService } from './product-all.service';

describe('ProductAllService', () => {
  let service: ProductAllService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductAllService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
