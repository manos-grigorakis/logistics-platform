import { Component, inject, OnInit } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import {
  FormArray,
  FormBuilder,
  FormControl,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CompanyProfileService } from '../../services/company-profile.service';
import { LanguageService } from '../../../../core/services/language.service';
import { finalize } from 'rxjs';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { CompanyProfileResponse } from '../../model/company-profile-response.interface';
import { CompanyProfileUpdateRequest } from '../../model/company-profile-update-request.interface';
import { BasicDetails } from '../../components/basic-details/basic-details';
import { Address } from '../../components/address/address';
import { Contact } from '../../components/contact/contact';
import { Branding } from '../../components/branding/branding';
import { PrimaryButton } from '../../../../shared/ui/primary-button/primary-button';
import { buildCompanyProfileForm } from '../../factory/company-profile-form.factory';

@Component({
  selector: 'app-edit-company-profile',
  imports: [
    TranslatePipe,
    LoadingSpinner,
    ReactiveFormsModule,
    BasicDetails,
    Address,
    Contact,
    Branding,
    PrimaryButton,
  ],
  templateUrl: './edit-company-profile.html',
  styleUrl: './edit-company-profile.css',
})
export class EditCompanyProfile implements OnInit {
  public isLoading: boolean = false;
  public companyProfile: CompanyProfileResponse | null = null;

  // Services
  private companyProfileService = inject(CompanyProfileService);
  private languageService = inject(LanguageService);

  private formBuilder = inject(FormBuilder);

  // Lifecycle
  ngOnInit() {
    this.fetchCompanyProfile();
    this.addPhone();
  }

  form = buildCompanyProfileForm(this.formBuilder, false);

  // Getters
  public get name(): FormControl<string> {
    return this.form.get('name') as FormControl;
  }

  public get vatPercentage(): FormControl<number> {
    return this.form.get('vatPercentage') as FormControl;
  }

  public get representativeTitle(): FormControl<string> {
    return this.form.get('representativeTitle') as FormControl;
  }

  public get representative(): FormControl<string> {
    return this.form.get('representative') as FormControl;
  }

  public get street(): FormControl<string> {
    return this.form.get('street') as FormControl;
  }

  public get streetNumber(): FormControl<string> {
    return this.form.get('streetNumber') as FormControl;
  }

  public get postalCode(): FormControl<string> {
    return this.form.get('postalCode') as FormControl;
  }

  public get region(): FormControl<string> {
    return this.form.get('region') as FormControl;
  }

  public get country(): FormControl<string> {
    return this.form.get('country') as FormControl;
  }

  public get brandPrimaryColor(): FormControl<string> {
    return this.form.get('brandPrimaryColor') as FormControl;
  }

  public get brandSecondaryColor(): FormControl<string> {
    return this.form.get('brandSecondaryColor') as FormControl;
  }

  public get logoFile(): FormControl<File> {
    return this.form.get('logoFile') as FormControl;
  }

  public get websiteUrl(): FormControl<string> {
    return this.form.get('websiteUrl') as FormControl;
  }

  public get slogan(): FormControl<string> {
    return this.form.get('slogan') as FormControl;
  }

  public get phones(): FormArray<FormControl<string>> {
    return this.form.get('phones') as FormArray<FormControl>;
  }

  public get email(): FormControl<string> {
    return this.form.get('email') as FormControl;
  }

  // Form array
  public addPhone(): void {
    this.phones.push(this.createPhone());
  }

  public removePhone(index: number): void {
    if (this.phones.length <= 1) return;
    this.phones.removeAt(index);
  }

  private createPhone() {
    return this.formBuilder.nonNullable.control('', [Validators.required]);
  }

  public onSubmitClick(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.form.updateValueAndValidity();
      return;
    }

    const raw = this.form.getRawValue();
    const request: CompanyProfileUpdateRequest = {
      ...raw,
      brandPrimaryColor: raw['brandPrimaryColor'] || null,
      brandSecondaryColor: raw['brandSecondaryColor'] || null,
      websiteUrl: raw['websiteUrl'] || null,
      slogan: raw['slogan'] || null,
    };

    console.log(request);

    this.updateCompanyProfile(request);
  }

  private updateCompanyProfile(request: CompanyProfileUpdateRequest): void {
    this.isLoading = true;

    this.companyProfileService
      .updateCompanyProfile(request)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => this.languageService.toastSuccess('company-profile.messages.success-update'),
        error: (err) => this.languageService.toastError(handleHttpErrors(err.status)),
      });
  }

  private fetchCompanyProfile(): void {
    this.isLoading = true;

    this.companyProfileService
      .fetchCompanyProfile()
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          this.companyProfile = res.data;
          this.patchCompanyProfileForm(this.companyProfile);
        },
        error: (err) => this.languageService.toastError(handleHttpErrors(err.status)),
      });
  }

  private patchCompanyProfileForm(data: CompanyProfileResponse): void {
    this.form.patchValue({
      name: data.name,
      vatPercentage: data.vatPercentage,
      representativeTitle: data.representativeTitle,
      representative: data.representative,
      street: data.address.street,
      streetNumber: data.address.streetNumber,
      postalCode: data.address.postalCode,
      region: data.address.region,
      country: data.address.country,
      brandPrimaryColor: data.branding.primaryColor ?? '',
      brandSecondaryColor: data.branding.secondaryColor ?? '',
      websiteUrl: data.websiteUrl ?? '',
      slogan: data.slogan ?? '',
      email: data.email,
      phones: data.phones,
    });
  }
}
