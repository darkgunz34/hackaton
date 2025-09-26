package fr.poc.hackaton.business.usecase;

import fr.poc.hackaton.business.dto.BankAccount;
import fr.poc.hackaton.business.dto.Transaction;
import fr.poc.hackaton.business.dto.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreateBankAccount {

    public BankAccount createBankAccount(String bankAccountId, Transaction transaction) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBankAccountId(bankAccountId);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        bankAccount.setTransactions(transactions);

        return bankAccount;
    }
}
