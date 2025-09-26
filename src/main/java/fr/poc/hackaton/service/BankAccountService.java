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
}
