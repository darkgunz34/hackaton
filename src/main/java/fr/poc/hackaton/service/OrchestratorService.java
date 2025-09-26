package fr.poc.hackaton.service;

import fr.poc.hackaton.business.dto.BankAccount;
import fr.poc.hackaton.business.dto.ClientSegment;
import fr.poc.hackaton.business.dto.User;
import fr.poc.hackaton.business.pojo.Balance;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class OrchestratorService {

    private final UserService userService;
    private final ClientSegmentService clientSegmentService;

    public List<Balance> findOperationsByUserAndDateRange(String id, LocalDate startDate, LocalDate endDate) {
        User user = this.userService.getUserById(id);
        if (user == null) {
            return null;
        }

        User userNew = new User();
        userNew.setBankAccounts(new ArrayList<>());

        List<Balance> lstBalance = null;
        for (BankAccount bankAccount : user.getBankAccounts()) {
            BankAccount bankAccountNew = new BankAccount();
            userNew.getBankAccounts().add(bankAccountNew);
            bankAccountNew.setId(bankAccount.getId());
            bankAccountNew.setTransactions(new ArrayList<>());
            bankAccountNew.setBankAccountId(bankAccount.getBankAccountId());

            for (var transaction : bankAccount.getTransactions()) {
                if (transaction.getDatePosted().isAfter(startDate) && transaction.getDatePosted().isBefore(endDate)) {
                    bankAccountNew.getTransactions().add(transaction);
                }
            }

            lstBalance = new ArrayList<>();

            for (var transaction : bankAccountNew.getTransactions()) {
                String categoryName = transaction.getCategory() != null ? transaction.getCategory().getInsightsCategoryName() : "uncategorized";
                Balance balance = lstBalance.stream()
                        .filter(b -> b.getNameCategory().equals(categoryName) && b.getDate().equals(transaction.getDatePosted().format(DateTimeFormatter.ofPattern("MM/yyyy"))))
                        .findFirst()
                        .orElse(null);
                if (balance == null) {
                    balance = new Balance();
                    String moisAnnee = transaction.getDatePosted().format(DateTimeFormatter.ofPattern("MM/yyyy"));
                    balance.setDate(moisAnnee);
                    balance.setNameCategory(categoryName);
                    balance.setCount(0);
                    balance.setSum(0.0);
                    lstBalance.add(balance);
                }
                balance.setSum(balance.getSum() + (transaction.getAmount() != null ? transaction.getAmount() : 0.0));
                balance.setCount(balance.getCount() + 1);
            }

            for (Balance balance : lstBalance) {
                if (balance.getCount() > 0) {
                    balance.setAverage(balance.getSum() / balance.getCount());
                } else {
                    balance.setAverage(0.0);
                }
            }
        }

        ClientSegment clientSegment = user.getClientSegment();

        List<User> usersSegement = this.userService.getBySegment(clientSegment);

        System.out.println(lstBalance);
        int nb = 0;
        for (User userSegement : usersSegement) {
            for (BankAccount bankAccount : userSegement.getBankAccounts()) {
                for (var transaction : bankAccount.getTransactions()) {
                    if (lstBalance.stream().anyMatch(b -> b.getNameCategory().equals(transaction.getCategory() != null ? transaction.getCategory().getInsightsCategoryName() : "uncategorized"))) {
                        Balance balance = lstBalance.stream()
                                .filter(b -> b.getNameCategory().equals(transaction.getCategory() != null ? transaction.getCategory().getInsightsCategoryName() : "uncategorized") && b.getDate().equals(transaction.getDatePosted().format(DateTimeFormatter.ofPattern("MM/yyyy"))))
                                .findFirst()
                                .orElse(null);
                        if (balance != null && transaction.getDatePosted().isAfter(startDate) && transaction.getDatePosted().isBefore(endDate)) {
                            nb++;
                            System.out.println("Transaction count : " + nb + transaction);
                            balance.setSumSegment(balance.getSumSegment() + (transaction.getAmount() != null ? transaction.getAmount() : 0.0));
                            balance.setCountSegment(balance.getCountSegment() + 1);
                        }
                    }
                }
            }
        }

        for (Balance balance : lstBalance) {
            balance.setAverageSegment(balance.getSumSegment() / balance.getCountSegment());
        }
        return lstBalance;
    }
}
