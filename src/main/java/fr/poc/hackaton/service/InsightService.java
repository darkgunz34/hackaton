package fr.poc.hackaton.service;

import fr.poc.hackaton.business.dto.Insight;
import fr.poc.hackaton.business.usecase.CreateInsight;
import fr.poc.hackaton.repository.InsightRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InsightService {

    private final CreateInsight createInsight;
    private final InsightRepository insightRepository;

    public synchronized Insight getInsight(String insightsCategoryName, String insightsThirdPartyName, String insightsMerchantLogoUrl) {
        Insight insight = this.insightRepository.findByInsightsCategoryNameAndInsightsThirdPartyName(insightsCategoryName, insightsThirdPartyName);
        if (insight == null) {
            insight = this.insightRepository.save(this.createInsight.createInsight(insightsCategoryName, insightsThirdPartyName, insightsMerchantLogoUrl));
        }
        return insight;
    }
}
