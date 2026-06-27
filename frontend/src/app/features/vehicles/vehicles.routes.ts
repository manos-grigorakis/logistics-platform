import { VehiclesPage } from './pages/vehicles-page/vehicles-page';
import { CreateVehicle } from './pages/create-vehicle/create-vehicle';
import { EditVehicle } from './pages/edit-vehicle/edit-vehicle';
import { Routes } from '@angular/router';

export default [
  { path: '', component: VehiclesPage, title: 'Vehicles' },
  { path: 'create-vehicle', component: CreateVehicle, title: 'Create Vehicle' },
  { path: 'edit-vehicle/:id', component: EditVehicle, title: 'Edit Vehicle' },
] satisfies Routes;
