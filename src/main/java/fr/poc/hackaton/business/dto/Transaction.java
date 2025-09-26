package fr.poc.hackaton.business.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
public class Transaction implements Serializable {

    @Id
    private String id;
    private LocalDate datePosted;
    private String trntype;
    private String originalLabel;
    private String thirdParty;
    private Double amount;
    private Boolean insightsMatched;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Insight insight;
}
