package fr.poc.hackaton.business.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class Category implements Serializable {
    @Id
    private String insightsCategoryId;

    private String insightsCategoryLogoUrl;
    private String insightsCategoryName;
}
