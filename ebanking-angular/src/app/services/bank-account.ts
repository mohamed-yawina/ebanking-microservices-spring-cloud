
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  BankAccount,
  BankAccountDetails,
  BankAccountPayload
} from '../models/bank-account';

@Injectable({
  providedIn: 'root'
})
export class BankAccountService {

  private http = inject(HttpClient);
  private apiUrl = '/api/accounts';

  getAccounts(): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(this.apiUrl);
  }

  getAccountById(id: string): Observable<BankAccount> {
    return this.http.get<BankAccount>(
      `${this.apiUrl}/${encodeURIComponent(id)}`
    );
  }

  getAccountDetails(id: string): Observable<BankAccountDetails> {
    return this.http.get<BankAccountDetails>(
      `${this.apiUrl}/${encodeURIComponent(id)}/details`
    );
  }

  getAccountsByCustomerId(
    customerId: number
  ): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(
      `${this.apiUrl}/customer/${customerId}`
    );
  }

  addAccount(
    account: BankAccountPayload
  ): Observable<BankAccount> {
    return this.http.post<BankAccount>(
      this.apiUrl,
      account
    );
  }

  updateAccount(
    id: string,
    account: BankAccountPayload
  ): Observable<BankAccount> {
    return this.http.put<BankAccount>(
      `${this.apiUrl}/${encodeURIComponent(id)}`,
      account
    );
  }

  deleteAccount(id: string): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${encodeURIComponent(id)}`
    );
  }
}
