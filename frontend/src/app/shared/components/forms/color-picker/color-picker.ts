import { Component, ElementRef, HostListener, inject, Input, OnInit } from '@angular/core';
import { ChromePickerComponent, ColorPickerControl } from '@iplab/ngx-color-picker';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-color-picker',
  imports: [ChromePickerComponent],
  templateUrl: './color-picker.html',
  styleUrl: './color-picker.css',
})
export class ColorPicker implements OnInit {
  @Input({ required: true }) label!: string;
  @Input({ required: true }) colorControl!: FormControl;
  @Input({ required: true }) colorValue!: ColorPickerControl;
  @Input() required: boolean = false;
  @Input() defaultColor?: string;

  private elementRef = inject(ElementRef);

  public showColorPicker: boolean = false;

  ngOnInit() {
    if (!this.colorControl.value) {
      this.colorControl.setValue(this.defaultColor);
    }
  }

  public togglePicker(): void {
    this.showColorPicker = !this.showColorPicker;
  }

  @HostListener('document:keydown.escape')
  onEscape() {
    this.showColorPicker = false;
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.showColorPicker = false;
    }
  }
}
