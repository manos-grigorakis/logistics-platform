import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CompanyProfileService } from '../../services/company-profile.service';
import { LanguageService } from '../../../../core/services/language.service';
import { FormArray, FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { buildCompanyProfileForm, createPhone } from '../../factory/company-profile-form.factory';
import { CompanyProfileCreateRequest } from '../../model/company-profile-create-request.interface';
import { Router } from '@angular/router';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { BasicDetails } from '../../components/basic-details/basic-details';
import { Address } from '../../components/address/address';
import { Contact } from '../../components/contact/contact';
import { Branding } from '../../components/branding/branding';
import { PrimaryButton } from '../../../../shared/ui/primary-button/primary-button';
import { TranslatePipe } from '@ngx-translate/core';
import { DetailedStepper } from '../../../../shared/ui/detailed-stepper/detailed-stepper';
import { Subscription, take } from 'rxjs';
import { ReviewStep } from '../../components/review-step/review-step';

@Component({
  selector: 'app-setup-company-profile',
  imports: [
    BasicDetails,
    ReactiveFormsModule,
    Address,
    Contact,
    Branding,
    PrimaryButton,
    TranslatePipe,
    DetailedStepper,
    ReviewStep,
  ],
  templateUrl: './setup-company-profile.html',
  styleUrl: './setup-company-profile.css',
})
export class SetupCompanyProfile implements OnInit, OnDestroy {
  public isLoading: boolean = false;
  public currentStep: number = 0;
  public defaultPrimaryColor: string = '#0f172a';
  public defaultSecondaryColor: string = '#2563eb';
  public steps: { title: string; description: string }[] = [];

  private router = inject(Router);
  private langChangeSub?: Subscription;

  // Services
  private companyProfileService = inject(CompanyProfileService);
  private languageService = inject(LanguageService);

  private formBuilder = inject(FormBuilder);
  private stepFields: Record<number, string[]> = {
    0: ['tin', 'name', 'vatPercentage', 'representativeTitle', 'representative'],
    1: ['street', 'streetNumber', 'postalCode', 'region', 'country'],
    2: ['email', 'phones'],
    3: ['brandPrimaryColor', 'brandSecondaryColor', 'websiteUrl', 'slogan'],
  };

  ngOnInit() {
    if (this.phones.length === 0) this.addPhone();

    this.setStepperValues();
    this.langChangeSub = this.languageService.onLangChange.subscribe(() => this.setStepperValues());
  }

  ngOnDestroy() {
    this.langChangeSub?.unsubscribe();
  }

  form = buildCompanyProfileForm(this.formBuilder, true);

  public next(): void {
    if (!this.isCurrentStepValid()) {
      this.markCurrentStepTouched();
      return;
    }

    this.currentStep++;
  }

  public previous(): void {
    if (this.currentStep > 0) {
      this.currentStep--;
    }
  }

  // Getters
  public get tin(): FormControl<string> {
    return this.form.get('tin') as unknown as FormControl<string>;
  }

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
    this.phones.push(createPhone(this.formBuilder));
  }

  public removePhone(index: number): void {
    if (this.phones.length <= 1) return;
    this.phones.removeAt(index);
  }

  public onSubmitClick(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.form.updateValueAndValidity();
      return;
    }

    const raw = this.form.getRawValue();
    const request: CompanyProfileCreateRequest = {
      ...raw,
      tin: this.tin.getRawValue(),
      vatPercentage: this.vatPercentage.getRawValue()!,
      country: this.country.getRawValue(),
      brandPrimaryColor: raw['brandPrimaryColor'] || null,
      brandSecondaryColor: raw['brandSecondaryColor'] || null,
      websiteUrl: raw['websiteUrl'] || null,
      slogan: raw['slogan'] || null,
    };

    this.createCompanyProfile(request);
  }

  private createCompanyProfile(request: CompanyProfileCreateRequest): void {
    this.companyProfileService.createCompanyProfile(request).subscribe({
      next: (res) => {
        this.languageService.toastSuccess('company-profile.messages.success');
        this.router.navigate(['/dashboard']);
      },
      error: (err) => this.languageService.toastError(handleHttpErrors(err.status)),
    });
  }

  private isCurrentStepValid(): boolean {
    return this.stepFields[this.currentStep].every((field) => {
      const control = this.form.get(field);
      return control?.valid;
    });
  }

  private markCurrentStepTouched(): void {
    this.stepFields[this.currentStep].forEach((field) => {
      this.form.get(field)?.markAsTouched();
      this.form.get(field)?.updateValueAndValidity();
    });
  }

  /**
   * Sets the stepper values using keys from translation
   */
  private setStepperValues(): void {
    const keys = [
      'company-profile.stepper.one.title',
      'company-profile.stepper.one.description',
      'company-profile.stepper.two.title',
      'company-profile.stepper.two.description',
      'company-profile.stepper.three.title',
      'company-profile.stepper.three.description',
      'company-profile.stepper.four.title',
      'company-profile.stepper.four.description',
      'company-profile.stepper.five.title',
      'company-profile.stepper.five.description',
    ];

    this.languageService
      .translateKeyAsync(keys)
      .pipe(take(1))
      .subscribe((translations: any) => {
        this.steps = [
          { title: translations[keys[0]], description: translations[keys[1]] },
          { title: translations[keys[2]], description: translations[keys[3]] },
          { title: translations[keys[4]], description: translations[keys[5]] },
          { title: translations[keys[6]], description: translations[keys[7]] },
          { title: translations[keys[8]], description: translations[keys[9]] },
        ];
      });
  }
}
