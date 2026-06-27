import { ShipmentsPage } from './pages/shipments-page/shipments-page';
import { ViewShipment } from './pages/view-shipment/view-shipment';
import { CreateShipment } from './pages/create-shipment/create-shipment';
import { EditShipment } from './pages/edit-shipment/edit-shipment';
import { Routes } from '@angular/router';

export default [
  { path: '', component: ShipmentsPage, title: 'Shipments' },
  { path: 'view-shipment/:id', component: ViewShipment, title: 'View Shipment' },
  { path: 'create-shipment', component: CreateShipment, title: 'Create Shipment' },
  { path: 'edit-shipment/:id', component: EditShipment, title: 'Edit Shipment' },
] satisfies Routes;
