package fr.poc.hackaton.init;

import com.opencsv.CSVReader;
import fr.poc.hackaton.business.dto.*;
import fr.poc.hackaton.service.*;
import jakarta.transaction.Transactional;
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

    private static final int MAX_THREADS = 5;
    private static final int BATCH_SIZE = 5000; // nombre de lignes traitées en une fois
    private static final int QUEUE_CAPACITY = 1000000;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("ExtractionService démarré à " + System.currentTimeMillis());

        BlockingQueue<String[]> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        for (int i = 0; i < MAX_THREADS; i++) {
            final int workerId = i + 1;
            executor.submit(() -> worker(queue, workerId));
        }

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("fichier.csv")))) {

            reader.readNext(); // saute l'entête
            String[] champs;
            long nb = 0;
            while ((champs = reader.readNext()) != null) {
                queue.put(champs);
                nb++;
                if (nb % 10000 == 0) {
                    System.out.println("Lignes lues : " + nb);
                }
            }
            System.out.println("nb full : " + nb);
        }

        // Ajout du poison pill pour chaque worker
        for (int i = 0; i < MAX_THREADS; i++) {
            queue.put(new String[0]); // poison pill
        }

        // Attendre la fin de tous les workers AVANT la fermeture du contexte
        executor.shutdown();
        boolean finished = executor.awaitTermination(24, TimeUnit.HOURS);
        if (finished) {
            System.out.println("Tous les workers ont terminé.");
        } else {
            System.out.println("Timeout : certains workers n'ont pas terminé !");
        }

        System.out.println("==> Extraction terminée !");
    }

    private void worker(BlockingQueue<String[]> queue, int workerId) {
        List<String[]> batch = new ArrayList<>(BATCH_SIZE);
        try {
            while (true) {
                String[] champs = queue.take(); // bloquant
                if (champs.length == 0) { // poison pill
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
        System.out.println("Worker #" + workerId + " a terminé (" + Thread.currentThread().getName() + ")");
    }

    @Transactional
    protected void traiterBatch(List<String[]> batch) {
        // 1) Extraire tous les DTOs du batch
        List<ExtractionDto> dtos = new ArrayList<>(batch.size());
        for (String[] champs : batch) {
            dtos.add(extractData(champs));
        }

        // 2) Charger en une requête les transactions existantes
        java.util.Set<String> ids = new java.util.HashSet<>();
        for (ExtractionDto dto : dtos) {
            if (dto.getTransactionId() != null) {
                ids.add(dto.getTransactionId());
            }
        }
        List<Transaction> existingList = transactionService.findAllByIds(ids);
        java.util.Map<String, Transaction> byId = new java.util.HashMap<>();
        for (Transaction t : existingList) {
            // suppose un getter d'identifiant "getId()"
            byId.put(t.getId(), t);
        }

        // 3) Construire uniquement les nouvelles transactions
        List<Transaction> toCreate = new ArrayList<>();
        for (ExtractionDto dto : dtos) {
            String id = dto.getTransactionId();
            if (id == null || byId.containsKey(id)) {
                continue;
            }
            Category category = processCategory(dto);
            Insight insight = processInsight(dto);
            Transaction t = transactionService.buildTransaction(
                    id,
                    dto.getDatePosted() != null ? java.time.LocalDate.parse(dto.getDatePosted()) : null,
                    dto.getTrntype(),
                    dto.getOriginalLabel(),
                    dto.getThirdParty(),
                    dto.getAmount() != null ? Double.parseDouble(dto.getAmount()) : null,
                    category,
                    insight
            );
            toCreate.add(t);
            byId.put(id, t); // référence utilisable immédiatement pour les liens compte/transaction
        }

        // 4) Sauvegarde en lot des nouvelles transactions
        if (!toCreate.isEmpty()) {
            transactionService.saveAll(toCreate);
            System.out.println("Transactions insérées en lot: " + toCreate.size());
        }

        // 5) Regrouper par compte bancaire pour limiter les saves
        java.util.Map<String, List<Transaction>> txByBankAccount = new java.util.HashMap<>();
        for (ExtractionDto dto : dtos) {
            String baId = dto.getBankAccountId();
            String txId = dto.getTransactionId();
            if (baId == null || txId == null) continue;
            Transaction tx = byId.get(txId);
            if (tx == null) continue;
            txByBankAccount.computeIfAbsent(baId, k -> new ArrayList<>()).add(tx);
        }

        // 6) Mise à jour des comptes bancaires et collecte des comptes pour la création des utilisateurs
        java.util.Map<String, BankAccount> bankAccounts = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, List<Transaction>> e : txByBankAccount.entrySet()) {
            BankAccount ba = bankAccountService.addTransactions(e.getKey(), e.getValue());
            bankAccounts.put(e.getKey(), ba);
        }

        // 7) Création/chargement des utilisateurs par couple unique (userId, bankAccount)
        java.util.Set<String> processedUserPairs = new java.util.HashSet<>();
        for (ExtractionDto dto : dtos) {
            String userId = dto.getUserId();
            String baId = dto.getBankAccountId();
            if (userId == null || baId == null) continue;
            String key = userId + "|" + baId;
            if (processedUserPairs.add(key)) {
                BankAccount ba = bankAccounts.get(baId);
                if (ba != null) {
                    userService.getUser(userId, ba);
                }
            }
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
