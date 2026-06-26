import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { REGEX } from '../../../shared/constants/regex.constant';

export function buildCompanyProfileForm(formBuilder: FormBuilder, includeTin: boolean = false) {
  const controls = {
    name: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(100)],
    }),
    vatPercentage: new FormControl<number | null>(null, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(1), Validators.max(100)],
    }),
    representativeTitle: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(50)],
    }),
    representative: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(150)],
    }),
    street: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(120)],
    }),
    streetNumber: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(10)],
    }),
    postalCode: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(10)],
    }),
    region: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(100)],
    }),
    country: new FormControl<string | null>(null, {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(100)],
    }),
    brandPrimaryColor: new FormControl<string>('', Validators.pattern(REGEX.HEX_COLOR)),
    brandSecondaryColor: new FormControl<string>('', Validators.pattern(REGEX.HEX_COLOR)),
    logoFile: new FormControl<File | null>(null),
    websiteUrl: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.maxLength(500), Validators.pattern(REGEX.URL)],
    }),
    slogan: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.maxLength(100)],
    }),
    phones: formBuilder.array<FormControl<string>>([createPhone(formBuilder)], Validators.required),
    email: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
  };

  if (includeTin) {
    Object.assign(controls, {
      tin: new FormControl<string>('', {
        nonNullable: true,
        validators: [Validators.required, Validators.minLength(9), Validators.maxLength(9)],
      }),
    });
  }

  return formBuilder.group(controls);
}

export function createPhone(formBuilder: FormBuilder) {
  return formBuilder.nonNullable.control('', Validators.required);
}
