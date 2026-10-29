
import { Routes } from '@angular/router';

import { Dashboard } from './pages/dashboard/dashboard';
import { Customers } from './pages/customers/customers';
import { Accounts } from './pages/accounts/accounts';
import { Chatbot } from './pages/chatbot/chatbot';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

  { path: 'dashboard', component: Dashboard },
  { path: 'customers', component: Customers },
  { path: 'accounts', component: Accounts },
  { path: 'chatbot', component: Chatbot },

  { path: '**', redirectTo: 'dashboard' }
];
