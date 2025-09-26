package fr.poc.hackaton.business.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class Insight implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer insightId;

    private String insightsCategoryName;
    private String insightsThirdPartyName;
    private String insightsMerchantLogoUrl;
}
