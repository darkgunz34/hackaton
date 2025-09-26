package fr.poc.hackaton.business.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class UserCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userCategoryId;


    @ManyToOne
    private User user;

    @ManyToOne
    private Category category;
    private String userChoise; // Peut-être, oui, non, plus jamais
    private LocalDate date;
}
