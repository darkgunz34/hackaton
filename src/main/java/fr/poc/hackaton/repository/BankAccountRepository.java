package fr.poc.hackaton.repository;


import fr.poc.hackaton.business.dto.BankAccount;
import fr.poc.hackaton.business.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {
    BankAccount findByBankAccountId(String bankAccountId);

}
