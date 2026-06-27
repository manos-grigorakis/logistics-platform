import { QuotesPage } from './pages/quotes-page/quotes-page';
import { CreateQuote } from './pages/create-quote/create-quote';
import { EditQuote } from './pages/edit-quote/edit-quote';
import { Routes } from '@angular/router';

export default [
  { path: '', component: QuotesPage, title: 'Quotes' },
  { path: 'create-quote', component: CreateQuote, title: 'Create Quote' },
  { path: 'edit-quote/:id', component: EditQuote, title: 'Quotes' },
] satisfies Routes;
