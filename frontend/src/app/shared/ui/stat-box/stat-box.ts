import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-stat-box',
  imports: [],
  templateUrl: './stat-box.html',
  styleUrl: './stat-box.css',
})
export class StatBox {
  @Input({ required: true }) label!: string;
  @Input({ required: true }) value!: string | null;
  @Input() styles?: string;
}
