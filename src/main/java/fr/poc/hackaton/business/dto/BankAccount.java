package fr.poc.hackaton.business.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Entity()
@Data
public class BankAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String bankAccountId;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Transaction> transactions;
}
