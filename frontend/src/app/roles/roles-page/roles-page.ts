import { Component } from '@angular/core';
import { RolesTable } from '../roles-table/roles-table';

@Component({
  selector: 'app-roles-page',
  imports: [RolesTable],
  templateUrl: './roles-page.html',
  styleUrl: './roles-page.css',
})
export class RolesPage {}
