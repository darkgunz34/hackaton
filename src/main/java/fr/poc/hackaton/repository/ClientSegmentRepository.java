package fr.poc.hackaton.repository;


import fr.poc.hackaton.business.dto.ClientSegment;
import fr.poc.hackaton.business.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientSegmentRepository extends JpaRepository<ClientSegment, String> {
    ClientSegment findClientSegmentById(String id);

}
