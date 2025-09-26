package fr.poc.hackaton.business.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Entity(name = "users")
@Data
public class User implements Serializable {

    @Id
    private String userId;

    @OneToMany(fetch = jakarta.persistence.FetchType.EAGER)
    private List<BankAccount> bankAccounts;

    @ManyToOne
    private ClientSegment clientSegment;
}
