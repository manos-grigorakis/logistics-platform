import { CmrDocumentsPage } from './pages/cmr-documents-page/cmr-documents-page';
import { UploadSignedCmr } from './pages/upload-signed-cmr/upload-signed-cmr';
import { Routes } from '@angular/router';

export default [
  { path: '', component: CmrDocumentsPage, title: 'CMR Documents' },
  { path: 'upload-signed', component: UploadSignedCmr, title: 'Upload Signed CMR Document' },
] satisfies Routes;
