package fr.poc.hackaton.service;

import fr.poc.hackaton.business.dto.Category;
import fr.poc.hackaton.business.dto.Insight;
import fr.poc.hackaton.business.dto.Transaction;
import fr.poc.hackaton.business.usecase.CreateTransaction;
import fr.poc.hackaton.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class TransactionService {

    private final CreateTransaction createTransaction;
    private final TransactionRepository TransactionRepository;

    public synchronized Transaction getTransaction(String id, LocalDate datePosted, String trntype, String originalLabel, String thirdParty, Double amount, Category category, Insight insight) {
        Transaction transaction = this.TransactionRepository.findTransactionById(id);
        if (transaction == null) {
            transaction = this.TransactionRepository.save(this.createTransaction.createTransaction(id, datePosted, trntype, originalLabel, thirdParty, amount, category, insight));
            System.out.println("Creating new transaction : " + id);
        }
        return transaction;
    }

    public Transaction getTransactionById(String id) {
        return this.TransactionRepository.findTransactionById(id);
    }
}
