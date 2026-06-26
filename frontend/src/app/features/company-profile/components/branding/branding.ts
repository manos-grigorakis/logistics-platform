import { Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { MainInput } from '../../../../shared/components/forms/main-input/main-input';
import { ColorPickerControl } from '@iplab/ngx-color-picker';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ColorPicker } from '../../../../shared/components/forms/color-picker/color-picker';
import { FileDropzone } from '../../../../shared/ui/file-dropzone/file-dropzone';
import { InfoBanner } from '../../../../shared/ui/info-banner/info-banner';

@Component({
  selector: 'app-branding',
  imports: [TranslatePipe, ReactiveFormsModule, MainInput, ColorPicker, FileDropzone, InfoBanner],
  templateUrl: './branding.html',
  styleUrl: './branding.css',
})
export class Branding implements OnInit {
  @Input({ required: true }) formUsage!: 'create' | 'update';
  @Input({ required: true }) parentForm!: FormGroup;
  @Input() logoUrl?: string | null;
  @Input() defaultPrimaryColor?: string;
  @Input() defaultSecondaryColor?: string;

  public brandPrimaryValColor: ColorPickerControl = new ColorPickerControl();
  public brandSecondaryValColor: ColorPickerControl = new ColorPickerControl();

  public supportedLogoTypes: string = '.png, .jpg, .jpeg';

  private destroyRef = inject(DestroyRef);

  ngOnInit() {
    this.brandPrimaryValColor.setValueFrom(this.brandPrimaryColor.getRawValue());
    this.brandSecondaryValColor.setValueFrom(this.brandSecondaryColor.getRawValue());

    this.brandPrimaryValColor.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((color) =>
        this.parentForm.patchValue({
          brandPrimaryColor: color.toHexString(),
        }),
      );

    this.brandSecondaryValColor.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((color) =>
        this.parentForm.patchValue({
          brandSecondaryColor: color.toHexString(),
        }),
      );
  }

  public get brandPrimaryColor(): FormControl<string> {
    return this.parentForm.get('brandPrimaryColor') as FormControl;
  }

  public get brandSecondaryColor(): FormControl<string> {
    return this.parentForm.get('brandSecondaryColor') as FormControl;
  }

  public setLogoFile(logo: File): void {
    this.parentForm.patchValue({ logoFile: logo });
  }
}
