package fr.poc.hackaton.business.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class ClientSegment implements Serializable {

    @Id
    private String id;
    private String name;
    private String salaryClass;
    private boolean hasChildren;
}
