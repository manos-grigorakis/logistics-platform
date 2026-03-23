import { Component, Input } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-detailed-stepper',
  imports: [NgClass],
  templateUrl: './detailed-stepper.html',
  styleUrl: './detailed-stepper.css',
})
export class DetailedStepper {
  @Input() steps: { title: string; description: string }[] = [];
  @Input() activeStep: number = 0;
}
