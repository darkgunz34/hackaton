package fr.poc.hackaton.business.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class Insight implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer insightId;

    @Column(columnDefinition = "text")
    private String insightsCategoryName;

    @Column(columnDefinition = "text")
    private String insightsThirdPartyName;

    @Column(columnDefinition = "text")
    private String insightsMerchantLogoUrl;
}
