package fr.poc.hackaton.service;

import fr.poc.hackaton.business.dto.Category;
import fr.poc.hackaton.business.dto.Insight;
import fr.poc.hackaton.business.dto.Transaction;
import fr.poc.hackaton.business.dto.User;
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
        }
        return transaction;
    }

    public Transaction getTransactionById(String id) {
        return this.TransactionRepository.findTransactionById(id);
    }

    // Récupération en masse des transactions existantes par leurs IDs
    public java.util.List<Transaction> findAllByIds(java.util.Set<String> ids) {
        return this.TransactionRepository.findAllById(ids);
    }

    // Sauvegarde en lot pour bénéficier du batching Hibernate
    public java.util.List<Transaction> saveAll(java.util.List<Transaction> transactions) {
        return this.TransactionRepository.saveAll(transactions);
    }

    // Construction d'une entité Transaction sans la persister (utilisé pour les insertions en lot)
    public Transaction buildTransaction(String id,
                                        LocalDate datePosted,
                                        String trntype,
                                        String originalLabel,
                                        String thirdParty,
                                        Double amount,
                                        Category category,
                                        Insight insight) {
        return this.createTransaction.createTransaction(id, datePosted, trntype, originalLabel, thirdParty, amount, category, insight);
    }
}
