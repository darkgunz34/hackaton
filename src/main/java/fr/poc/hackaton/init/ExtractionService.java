package fr.poc.hackaton.init;

import com.opencsv.CSVReader;
import fr.poc.hackaton.business.dto.*;
import fr.poc.hackaton.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RequiredArgsConstructor
@Service
public class ExtractionService implements CommandLineRunner {

    private final CategoryService categoryService;
    private final InsightService insightService;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    private final UserService userService;

    private static final int MAX_THREADS = 10;
    private static final int BATCH_SIZE = 1000; // nombre de lignes traitées en une fois
    private static final int QUEUE_CAPACITY = 10000;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("ExtractionService démarré à " + System.currentTimeMillis());

        BlockingQueue<String[]> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        // 1. Démarre les workers
        for (int i = 0; i < MAX_THREADS; i++) {
            executor.submit(() -> worker(queue));
        }

        // 2. Lecture du CSV et injection dans la file
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("fichier.csv")))) {

            reader.readNext(); // saute l'entête
            String[] champs;
            long nb = 0;
            while ((champs = reader.readNext()) != null) {
                queue.put(champs);
                nb++;
                if (nb % 100000 == 0) {
                    System.out.println("Lignes lues : " + nb);
                }
            }
        }

        // 3. On arrête les workers après la fin
        executor.shutdown();
        executor.awaitTermination(24, TimeUnit.HOURS);

        System.out.println("==> Extraction terminée !");
    }

    private void worker(BlockingQueue<String[]> queue) {
        List<String[]> batch = new ArrayList<>(BATCH_SIZE);
        try {
            while (true) {
                String[] champs = queue.poll(2, TimeUnit.SECONDS);
                if (champs == null) {
                    // plus rien à lire => vider le batch restant
                    if (!batch.isEmpty()) {
                        traiterBatch(batch);
                        batch.clear();
                    }
                    break;
                }

                batch.add(champs);

                if (batch.size() >= BATCH_SIZE) {
                    traiterBatch(batch);
                    batch.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void traiterBatch(List<String[]> batch) {
        for (String[] champs : batch) {
            ExtractionDto dto = extractData(champs);

            Category category = processCategory(dto);
            Insight insight = processInsight(dto);
            Transaction transaction = processTransaction(dto, category, insight);
            BankAccount bankAccount = getBankAccount(dto.getBankAccountId(), transaction);
            getUser(dto.getUserId(), bankAccount);
        }
        System.out.println("Batch de " + batch.size() + " lignes traité par " + Thread.currentThread().getName());
    }

    private ExtractionDto extractData(String[] champs) {
        ExtractionDto dto = new ExtractionDto();
        dto.setUserId(getChamp(champs, 0));
        dto.setTransactionId(getChamp(champs, 1));
        dto.setBankAccountId(getChamp(champs, 2));
        dto.setFinancialInstitutionId(getChamp(champs, 3));
        dto.setDatePosted(getChamp(champs, 4));
        dto.setTrntype(getChamp(champs, 5));
        dto.setBankAccountType(getChamp(champs, 6));
        dto.setCoalesce(getChamp(champs, 7));
        dto.setBalance(getChamp(champs, 8));
        dto.setOriginalLabel(getChamp(champs, 9));
        dto.setThirdParty(getChamp(champs, 10));
        dto.setCategory(getChamp(champs, 11));
        dto.setAmount(getChamp(champs, 12));
        dto.setCountry(getChamp(champs, 13));
        dto.setPostcode(getChamp(champs, 14));
        dto.setCity(getChamp(champs, 15));
        dto.setInsightsIdentifier(getChamp(champs, 16));
        dto.setInsightsMatched(getChamp(champs, 17));
        dto.setInsightsCategoryName(getChamp(champs, 18));
        dto.setInsightsCategoryId(getChamp(champs, 19));
        dto.setInsightsCategoryLogoUrl(getChamp(champs, 20));
        dto.setInsightsThirdPartyName(getChamp(champs, 21));
        dto.setInsightsMerchantLogoUrl(getChamp(champs, 22));
        return dto;
    }

    private String getChamp(String[] champs, int index) {
        return index < champs.length ? champs[index] : null;
    }

    private Category processCategory(ExtractionDto dto) {
        return categoryService.getCategory(dto.getInsightsCategoryId(), dto.getInsightsCategoryName(), dto.getInsightsCategoryLogoUrl());
    }

    private Insight processInsight(ExtractionDto dto) {
        return insightService.getInsight(dto.getInsightsCategoryName(), dto.getInsightsThirdPartyName(), dto.getInsightsMerchantLogoUrl());
    }

    private Transaction processTransaction(ExtractionDto dto, Category category, Insight insight) {
        return transactionService.getTransaction(
                dto.getTransactionId(),
                dto.getDatePosted() != null ? java.time.LocalDate.parse(dto.getDatePosted()) : null,
                dto.getTrntype(),
                dto.getOriginalLabel(),
                dto.getThirdParty(),
                dto.getAmount() != null ? Double.parseDouble(dto.getAmount()) : null,
                category,
                insight
        );
    }

    private BankAccount getBankAccount(String bankAccountId, Transaction transaction) {
        return bankAccountService.getBankAccount(bankAccountId, transaction);
    }

    private User getUser(String userId, BankAccount bankAccount) {
        return userService.getUser(userId, bankAccount);
    }
}
