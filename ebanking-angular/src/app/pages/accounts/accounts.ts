
import {
  Component,
  OnInit,
  ChangeDetectorRef,
  inject
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { DecimalPipe, DatePipe } from '@angular/common';

import { CustomerService } from '../../services/customer';
import { BankAccountService } from '../../services/bank-account';

import { Customer } from '../../models/customer';

import {
  BankAccount,
  BankAccountDetails,
  BankAccountPayload,
  AccountType
} from '../../models/bank-account';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './accounts.html',
  styleUrl: './accounts.css'
})
export class Accounts implements OnInit {

  private accountService = inject(BankAccountService);
  private customerService = inject(CustomerService);
  private cdr = inject(ChangeDetectorRef);

  accounts: BankAccount[] = [];
  customers: Customer[] = [];

  loading = false;
  saving = false;
  detailsLoading = false;

  error = '';
  success = '';
  searchTerm = '';
  typeFilter = 'ALL';

  showForm = false;
  editing = false;
  editingAccountId = '';

  selectedAccount: BankAccountDetails | null = null;

  formAccount: BankAccountPayload = {
    balance: 0,
    type: 'CURRENT_ACCOUNT',
    customerId: 0
  };

  ngOnInit(): void {
    this.loadAccounts();
    this.loadCustomers();
  }

  get totalAccounts(): number {
    return this.accounts.length;
  }

  get currentAccounts(): number {
    return this.accounts.filter(
      a => a.type === 'CURRENT_ACCOUNT'
    ).length;
  }

  get savingAccounts(): number {
    return this.accounts.filter(
      a => a.type === 'SAVING_ACCOUNT'
    ).length;
  }

  get totalBalance(): number {
    return this.accounts.reduce(
      (sum, account) => sum + account.balance,
      0
    );
  }

  get filteredAccounts(): BankAccount[] {
    const term = this.searchTerm.toLowerCase().trim();

    return this.accounts.filter(account => {
      const customer = this.customers.find(
        c => c.id === account.customerId
      );

      const matchesSearch =
        account.id.toLowerCase().includes(term) ||
        account.customerId.toString().includes(term) ||
        (customer?.name.toLowerCase().includes(term) ?? false);

      const matchesType =
        this.typeFilter === 'ALL' ||
        account.type === this.typeFilter;

      return matchesSearch && matchesType;
    });
  }

  getCustomerName(customerId: number): string {
    return this.customers.find(
      c => c.id === customerId
    )?.name ?? `Client #${customerId}`;
  }

  loadAccounts(): void {
    this.loading = true;
    this.error = '';
    this.cdr.markForCheck();

    this.accountService.getAccounts().subscribe({
      next: data => {
        this.accounts = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        console.error('Erreur comptes :', err);
        this.error = 'Impossible de charger les comptes.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadCustomers(): void {
    this.customerService.getCustomers().subscribe({
      next: data => {
        this.customers = data;
        this.cdr.detectChanges();
      },
      error: err => {
        console.error('Erreur clients :', err);
        this.error = 'Impossible de charger les clients.';
        this.cdr.detectChanges();
      }
    });
  }

  openAddForm(): void {
    this.editing = false;
    this.editingAccountId = '';
    this.showForm = true;
    this.selectedAccount = null;
    this.error = '';
    this.success = '';

    this.formAccount = {
      balance: 0,
      type: 'CURRENT_ACCOUNT',
      customerId: this.customers[0]?.id ?? 0
    };
  }

  openEditForm(account: BankAccount): void {
    this.editing = true;
    this.editingAccountId = account.id;
    this.showForm = true;
    this.selectedAccount = null;
    this.error = '';
    this.success = '';

    this.formAccount = {
      balance: account.balance,
      type: account.type,
      customerId: account.customerId
    };
  }

  cancelForm(): void {
    this.showForm = false;
  }

  saveAccount(): void {
    if (
      !Number.isFinite(this.formAccount.balance) ||
      this.formAccount.balance < 0 ||
      !this.customers.some(
        c => c.id === Number(this.formAccount.customerId)
      )
    ) {
      this.error =
        'Veuillez saisir un solde valide et sélectionner un client.';
      return;
    }

    this.saving = true;
    this.error = '';
    this.success = '';
    this.cdr.markForCheck();

    const payload: BankAccountPayload = {
      balance: Number(this.formAccount.balance),
      type: this.formAccount.type,
      customerId: Number(this.formAccount.customerId)
    };

    const request = this.editing
      ? this.accountService.updateAccount(
          this.editingAccountId, payload
        )
      : this.accountService.addAccount(payload);

    request.subscribe({
      next: () => {
        this.saving = false;
        this.showForm = false;

        this.success = this.editing
          ? 'Compte modifié avec succès.'
          : 'Compte créé avec succès.';

        this.cdr.detectChanges();
        this.loadAccounts();
      },
      error: err => {
        console.error('Erreur enregistrement :', err);
        this.error = "Impossible d'enregistrer le compte.";
        this.saving = false;
        this.cdr.detectChanges();
      }
    });
  }

  viewDetails(account: BankAccount): void {
    this.selectedAccount = null;
    this.detailsLoading = true;
    this.error = '';
    this.cdr.markForCheck();

    this.accountService.getAccountDetails(account.id).subscribe({
      next: data => {
        this.selectedAccount = data;
        this.detailsLoading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        console.error('Erreur détails :', err);
        this.error = 'Impossible de charger les détails du compte.';
        this.detailsLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  closeDetails(): void {
    this.selectedAccount = null;
  }

  deleteAccount(account: BankAccount): void {
    if (!confirm(`Supprimer le compte ${account.id} ?`)) {
      return;
    }

    this.error = '';
    this.success = '';

    this.accountService.deleteAccount(account.id).subscribe({
      next: () => {
        this.success = 'Compte supprimé avec succès.';
        this.selectedAccount = null;
        this.cdr.detectChanges();
        this.loadAccounts();
      },
      error: err => {
        console.error('Erreur suppression :', err);
        this.error = 'Impossible de supprimer le compte.';
        this.cdr.detectChanges();
      }
    });
  }
}
