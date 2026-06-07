/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { CmrDocumentsService } from './cmr-documents.service';

describe('Service: CmrDocuments', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CmrDocumentsService]
    });
  });

  it('should ...', inject([CmrDocumentsService], (service: CmrDocumentsService) => {
    expect(service).toBeTruthy();
  }));
});
