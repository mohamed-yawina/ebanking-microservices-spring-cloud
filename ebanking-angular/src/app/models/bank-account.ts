
import { Customer } from './customer';

export type AccountType =
  | 'CURRENT_ACCOUNT'
  | 'SAVING_ACCOUNT';

export interface BankAccount {
  id: string;
  balance: number;
  createdAt: string;
  type: AccountType;
  customerId: number;
}

export interface BankAccountDetails extends BankAccount {
  customer: Customer | null;
}

export interface BankAccountPayload {
  balance: number;
  type: AccountType;
  customerId: number;
}
