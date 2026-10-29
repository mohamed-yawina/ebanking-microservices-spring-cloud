
import {
  Component,
  OnInit,
  ChangeDetectorRef,
  inject
} from '@angular/core';

import { FormsModule } from '@angular/forms';

import { CustomerService } from '../../services/customer';
import { Customer } from '../../models/customer';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './customers.html',
  styleUrl: './customers.css'
})
export class Customers implements OnInit {

  private customerService = inject(CustomerService);
  private cdr = inject(ChangeDetectorRef);

  // Liste des clients
  customers: Customer[] = [];

  // Recherche
  searchTerm = '';

  // États
  loading = false;
  saving = false;
  error = '';
  success = '';

  // Formulaire
  showForm = false;
  editing = false;

  formCustomer: Customer = {
    id: 0,
    name: '',
    email: ''
  };

  // Initialisation
  ngOnInit(): void {
    this.loadCustomers();
  }

  // Filtrer les clients
  get filteredCustomers(): Customer[] {
    const term = this.searchTerm.toLowerCase().trim();

    return this.customers.filter(customer =>
      customer.name.toLowerCase().includes(term) ||
      customer.email.toLowerCase().includes(term) ||
      customer.id.toString().includes(term)
    );
  }

  // Charger tous les clients
  loadCustomers(): void {
    console.log('Chargement des clients...');

    this.loading = true;
    this.error = '';
    this.cdr.markForCheck();

    this.customerService.getCustomers().subscribe({
      next: (data) => {
        console.log('Clients reçus :', data);

        this.customers = data;
        this.loading = false;

        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur API :', err);

        this.error = 'Impossible de charger les clients.';
        this.loading = false;

        this.cdr.detectChanges();
      }
    });
  }

  // Ouvrir formulaire d'ajout
  openAddForm(): void {
    this.editing = false;
    this.showForm = true;

    this.error = '';
    this.success = '';

    this.formCustomer = {
      id: 0,
      name: '',
      email: ''
    };
  }

  // Ouvrir formulaire de modification
  openEditForm(customer: Customer): void {
    this.editing = true;
    this.showForm = true;

    this.error = '';
    this.success = '';

    this.formCustomer = { ...customer };
  }

  // Annuler le formulaire
  cancelForm(): void {
    this.showForm = false;
  }

  // Ajouter ou modifier un client
  saveCustomer(): void {

    if (
      !this.formCustomer.name.trim() ||
      !this.formCustomer.email.trim()
    ) {
      this.error = 'Veuillez remplir tous les champs.';
      return;
    }

    this.saving = true;
    this.error = '';
    this.success = '';

    this.cdr.markForCheck();

    const request = this.editing
      ? this.customerService.updateCustomer(this.formCustomer)
      : this.customerService.addCustomer({
          name: this.formCustomer.name,
          email: this.formCustomer.email
        });

    request.subscribe({
      next: () => {
        this.saving = false;
        this.showForm = false;

        this.success = this.editing
          ? 'Client modifié avec succès.'
          : 'Client ajouté avec succès.';

        this.cdr.detectChanges();

        // Actualiser la liste
        this.loadCustomers();
      },
      error: (err) => {
        console.error('Erreur enregistrement :', err);

        this.error =
          "Erreur lors de l'enregistrement du client.";

        this.saving = false;

        this.cdr.detectChanges();
      }
    });
  }

  // Supprimer un client
  deleteCustomer(customer: Customer): void {

    const confirmed = confirm(
      `Voulez-vous vraiment supprimer ${customer.name} ?`
    );

    if (!confirmed) {
      return;
    }

    this.error = '';
    this.success = '';

    this.customerService.deleteCustomer(customer.id).subscribe({
      next: () => {
        this.success = 'Client supprimé avec succès.';

        this.cdr.detectChanges();

        // Actualiser la liste
        this.loadCustomers();
      },
      error: (err) => {
        console.error('Erreur suppression :', err);

        this.error = 'Impossible de supprimer ce client.';

        this.cdr.detectChanges();
      }
    });
  }
}
