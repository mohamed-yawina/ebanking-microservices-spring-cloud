
import {
  Component,
  OnInit,
  ChangeDetectorRef,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';

import { CustomerService } from '../../services/customer';
import { BankAccountService } from '../../services/bank-account';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  private customerService = inject(CustomerService);
  private accountService = inject(BankAccountService);
  private cdr = inject(ChangeDetectorRef);

  // Statistiques
  totalCustomers: number = 0;
  totalAccounts: number = 0;
  totalBalance: number = 0;

  currentAccounts: number = 0;
  savingAccounts: number = 0;

  // États de l'interface
  loading: boolean = true;
  error: string = '';

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {

    console.log('Chargement du Dashboard...');

    this.loading = true;
    this.error = '';

    forkJoin({
      customers: this.customerService.getCustomers(),
      accounts: this.accountService.getAccounts()
    }).subscribe({

      next: ({ customers, accounts }) => {

        console.log('Dashboard - Clients :', customers);
        console.log('Dashboard - Comptes :', accounts);

        // Nombre total de clients
        this.totalCustomers = customers.length;

        // Nombre total de comptes
        this.totalAccounts = accounts.length;

        // Solde total de tous les comptes
        this.totalBalance = accounts.reduce(
          (total, account) => total + account.balance,
          0
        );

        // Nombre de comptes courants
        this.currentAccounts = accounts.filter(
          account => account.type === 'CURRENT_ACCOUNT'
        ).length;

        // Nombre de comptes épargne
        this.savingAccounts = accounts.filter(
          account => account.type === 'SAVING_ACCOUNT'
        ).length;

        // Fin du chargement
        this.loading = false;
        this.error = '';

        console.log('Dashboard chargé avec succès !');

        // Correction du problème d'affichage Angular
        this.cdr.detectChanges();
      },

      error: (err) => {

        console.error('Erreur Dashboard :', err);

        this.error =
          'Impossible de charger les données du Dashboard.';

        this.loading = false;

        // Actualiser l'interface même en cas d'erreur
        this.cdr.detectChanges();
      }
    });
  }

  // Bouton pour actualiser les statistiques
  refreshDashboard(): void {
    this.loadDashboard();
  }
}
