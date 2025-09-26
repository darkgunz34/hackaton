package fr.poc.hackaton.business.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
public class ClientSegment implements Serializable {

    @Id
    private String id;
    private String name;
    private String salaryClass;
    private List<User> users;
    private boolean hasChildren;
}
