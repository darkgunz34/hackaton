package fr.poc.hackaton.business.usecase;

import fr.poc.hackaton.business.dto.Category;
import fr.poc.hackaton.business.dto.Insight;
import fr.poc.hackaton.business.dto.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CreateTransaction {

    public Transaction createTransaction(String id, LocalDate datePosted, String trntype, String originalLabel, String thirdParty, Double amount, Category category, Insight insight) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setDatePosted(datePosted);
        transaction.setTrntype(trntype);
        transaction.setOriginalLabel(originalLabel);
        transaction.setThirdParty(thirdParty);
        transaction.setAmount(amount);
        if (insight != null) {
            transaction.setInsightsMatched(true);
        }
        transaction.setCategory(category);
        transaction.setInsight(insight);
        return transaction;
    }
}
