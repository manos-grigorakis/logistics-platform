import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
} from '@angular/forms';
import { NgClass } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-main-input',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgClass, TranslatePipe],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: MainInput,
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: MainInput,
      multi: true,
    },
  ],
  templateUrl: './main-input.html',
  styleUrl: './main-input.css',
})
export class MainInput implements ControlValueAccessor, Validator {
  public readonly showRequiredStar = input<boolean>(false);
  public readonly label = input<string>('');
  public readonly placeholder = input<string>('');
  public readonly type = input<
    'text' | 'number' | 'email' | 'password' | 'date' | 'datetime-local' | 'checkbox'
  >('text');

  public readonly value = signal<string | boolean>('');
  public readonly disabled = signal(false);
  public readonly withErrors = signal(false);
  public readonly patternErrorMessage = input<string>('');
  public readonly displayErrors = input<boolean>(true);
  public readonly highlightBorders = input<boolean>(false);

  public control: AbstractControl | null = null;

  private onChange: (value: string | boolean) => void = () => {};
  private onTouched: () => void = () => {};

  public writeValue(value: string): void {
    this.value.set(value ?? '');
  }

  public registerOnChange(fn: (value: string | boolean) => void): void {
    this.onChange = fn;
  }

  public registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  public setDisabledState(isDisabled: boolean): void {
    this.disabled.set(isDisabled);
  }

  public onInput(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (this.type() === 'checkbox') {
      this.value.set(input.checked);
      this.onChange(input.checked);
      return;
    }

    this.value.set(input.value);
    this.onChange(input.value);
  }

  public onBlur(): void {
    this.onTouched();
  }

  public validate(control: AbstractControl): ValidationErrors | null {
    queueMicrotask(() => {
      this.withErrors.set(control.invalid && (control.dirty || control.touched));
      this.control = control;
    });
    return null;
  }
}
