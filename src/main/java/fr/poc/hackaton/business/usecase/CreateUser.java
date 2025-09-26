package fr.poc.hackaton.business.usecase;

import fr.poc.hackaton.business.dto.BankAccount;
import fr.poc.hackaton.business.dto.Transaction;
import fr.poc.hackaton.business.dto.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreateUser {

    public User createUser(String userId, BankAccount bankAccount) {
        User user = new User();
        user.setUserId(userId);
        List<BankAccount> bankAccounts = new ArrayList<>();
        bankAccounts.add(bankAccount);
        user.setBankAccounts(bankAccounts);
        return user;
    }
}
