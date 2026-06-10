import { Component, inject } from '@angular/core';
import { FileDropzone } from '../../../../shared/ui/file-dropzone/file-dropzone';
import { TranslatePipe } from '@ngx-translate/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CmrDocumentsService } from '../../cmr-documents.service';
import { UploadSignedCmrDocumentRequest } from '../../models/upload-signed-cmr-document-request.interface';
import { LanguageService } from '../../../../core/services/language.service';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { Router } from '@angular/router';
import { MainInput } from '../../../../shared/components/forms/main-input/main-input';
import { PrimaryButton } from '../../../../shared/ui/primary-button/primary-button';
import { NgIcon } from '@ng-icons/core';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-upload-signed-cmr',
  imports: [FileDropzone, TranslatePipe, MainInput, ReactiveFormsModule, PrimaryButton, NgIcon],
  templateUrl: './upload-signed-cmr.html',
  styleUrl: './upload-signed-cmr.css',
})
export class UploadSignedCmr {
  public isLoading: boolean = false;

  private formBuilder = inject(FormBuilder);
  private router = inject(Router);

  // Services
  private cmrDocumentsService = inject(CmrDocumentsService);
  private languageService = inject(LanguageService);

  form = this.formBuilder.group({
    senderSigned: new FormControl<boolean>(false, Validators.requiredTrue),
    carrierSigned: new FormControl<boolean>(false, Validators.requiredTrue),
    consigneeSigned: new FormControl<boolean>(false, Validators.requiredTrue),
    cmrFile: new FormControl<File | null>(null, Validators.required),
  });

  // Getters
  get senderSigned(): FormControl {
    return this.form.get('senderSigned') as FormControl;
  }

  get carrierSigned(): FormControl {
    return this.form.get('carrierSigned') as FormControl;
  }

  get consigneeSigned(): FormControl {
    return this.form.get('consigneeSigned') as FormControl;
  }

  get cmrFile(): FormControl {
    return this.form.get('cmrFile') as FormControl;
  }

  public onCmrDocumentFileSelected(file: File): void {
    this.cmrFile.setValue(file);
  }

  public onFormSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.senderSigned.updateValueAndValidity();
      this.carrierSigned.updateValueAndValidity();
      this.consigneeSigned.updateValueAndValidity();
      return;
    }

    this.isLoading = true;

    const { senderSigned, carrierSigned, consigneeSigned, cmrFile } = this.form.getRawValue();
    const payload: UploadSignedCmrDocumentRequest = {
      senderSigned: senderSigned!,
      carrierSigned: carrierSigned!,
      consigneeSigned: consigneeSigned!,
      file: cmrFile!,
    };

    this.cmrDocumentsService
      .uploadSignedCmrDocument(payload)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.languageService.toastSuccess('cmr-documents.messages.success-signed-upload');
          this.router.navigate(['cmr-documents']);
        },
        error: (err) => {
          const status = err.status;
          const errorCode = err.error.error.errorCode;

          if (status === 404) {
            this.languageService.toastError('cmr-documents.messages.not-found');
          } else if (status === 400 && errorCode === 'INVALID_DOCUMENT') {
            this.languageService.toastError('common.errors.files-processing');
          } else if (status === 400 && errorCode === 'MISSING_QR_CODE') {
            this.languageService.toastError('cmr-documents.messages.invalid-qr-code');
          } else if (status === 409 && errorCode === 'ALREADY_SIGNED') {
            this.languageService.toastError('cmr-documents.messages.already-signed');
          } else if (status === 409 && errorCode === 'INVALID_STATUS_TRANSITION') {
            this.languageService.toastError('cmr-documents.messages.status-update-violation');
          } else {
            this.languageService.toastError(handleHttpErrors(status));
          }
        },
      });
  }
}
