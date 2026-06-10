export interface UploadSignedCmrDocumentRequest {
  senderSigned: boolean;
  carrierSigned: boolean;
  consigneeSigned: boolean;
  file: File;
}
