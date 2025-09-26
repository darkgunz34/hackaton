package fr.poc.hackaton.business.usecase;

import fr.poc.hackaton.business.dto.Insight;
import org.springframework.stereotype.Service;

@Service
public class CreateInsight {

    public Insight createInsight(String insightsCategoryName, String insightsThirdPartyName, String insightsMerchantLogoUrl) {
        Insight insight = new Insight();
        insight.setInsightsCategoryName(insightsCategoryName);
        insight.setInsightsThirdPartyName(insightsThirdPartyName);
        insight.setInsightsMerchantLogoUrl(insightsMerchantLogoUrl);
        return insight;
    }
}
