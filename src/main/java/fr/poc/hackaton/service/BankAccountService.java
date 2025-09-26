package fr.poc.hackaton.service;

import fr.poc.hackaton.business.dto.BankAccount;
import fr.poc.hackaton.business.dto.Transaction;
import fr.poc.hackaton.business.dto.BankAccount;
import fr.poc.hackaton.business.dto.User;
import fr.poc.hackaton.business.usecase.CreateBankAccount;
import fr.poc.hackaton.repository.BankAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BankAccountService {

    private final CreateBankAccount createBankAccount;
    private final BankAccountRepository bankAccountRepository;

    public synchronized BankAccount getBankAccount(String bankAccountId, Transaction transaction) {
        BankAccount bankAccount = this.bankAccountRepository.findByBankAccountId(bankAccountId);
        if(bankAccount == null) {
            bankAccount = this.bankAccountRepository.save(this.createBankAccount.createBankAccount(bankAccountId, transaction));
            System.out.println("Creating new bank account : " + bankAccountId);
        }else{
            if(!bankAccount.getTransactions().contains(transaction)){
                bankAccount.getTransactions().add(transaction);
                this.bankAccountRepository.save(bankAccount);
            }
        }
        return bankAccount;
    }

    // Ajoute une liste de transactions à un compte bancaire (création si nécessaire), avec un seul save final
    public synchronized BankAccount addTransactions(String bankAccountId, java.util.List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return this.bankAccountRepository.findByBankAccountId(bankAccountId);
        }
        BankAccount bankAccount = this.bankAccountRepository.findByBankAccountId(bankAccountId);
        if (bankAccount == null) {
            // Crée le compte avec la première transaction puis ajoute le reste
            Transaction first = transactions.get(0);
            bankAccount = this.bankAccountRepository.save(this.createBankAccount.createBankAccount(bankAccountId, first));
            if (transactions.size() > 1) {
                for (int i = 1; i < transactions.size(); i++) {
                    Transaction t = transactions.get(i);
                    if (!bankAccount.getTransactions().contains(t)) {
                        bankAccount.getTransactions().add(t);
                    }
                }
                bankAccount = this.bankAccountRepository.save(bankAccount);
            }
            System.out.println("Creating new bank account : " + bankAccountId);
        } else {
            boolean changed = false;
            for (Transaction t : transactions) {
                if (!bankAccount.getTransactions().contains(t)) {
                    bankAccount.getTransactions().add(t);
                    changed = true;
                }
            }
            if (changed) {
                bankAccount = this.bankAccountRepository.save(bankAccount);
            }
        }
        return bankAccount;
    }
}
