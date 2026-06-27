import { UsersPage } from './pages/users-page/users-page';
import { CreateUser } from './pages/create-user/create-user';
import { EditUser } from './pages/edit-user/edit-user';
import { Routes } from '@angular/router';

export default [
  { path: '', component: UsersPage, title: 'Users' },
  { path: 'create-user', component: CreateUser, title: 'Create User' },
  { path: 'edit-user/:id', component: EditUser, title: 'Edit User' },
] satisfies Routes;
