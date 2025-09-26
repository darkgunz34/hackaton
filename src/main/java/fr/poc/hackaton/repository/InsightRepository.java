package fr.poc.hackaton.repository;

import fr.poc.hackaton.business.dto.Insight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsightRepository extends JpaRepository<Insight, Integer> {

    Insight findByInsightsCategoryName(String insightsCategoryName);
}
