import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-branding',
  imports: [TranslatePipe, ReactiveFormsModule],
  templateUrl: './branding.html',
  styleUrl: './branding.css',
})
export class Branding {
  @Input({ required: true }) parentForm!: FormGroup;
}
