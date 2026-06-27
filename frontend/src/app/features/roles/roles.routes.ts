import { EditRole } from './pages/edit-role/edit-role';
import { RolesPage } from './pages/roles-page/roles-page';
import { CreateRole } from './pages/create-role/create-role';
import { Routes } from '@angular/router';

export default [
  { path: '', component: RolesPage, title: 'Roles' },
  { path: 'create-role', component: CreateRole, title: 'Create Role' },
  { path: 'edit-role/:id', component: EditRole, title: 'Edit Role' },
] satisfies Routes;
