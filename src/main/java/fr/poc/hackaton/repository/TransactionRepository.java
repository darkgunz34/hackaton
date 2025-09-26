package fr.poc.hackaton.repository;

import fr.poc.hackaton.business.dto.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Transaction findTransactionById(String id);
}
