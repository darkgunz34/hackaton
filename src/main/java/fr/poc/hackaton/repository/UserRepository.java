package fr.poc.hackaton.repository;


import fr.poc.hackaton.business.dto.ClientSegment;
import fr.poc.hackaton.business.dto.Transaction;
import fr.poc.hackaton.business.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUserId(String id);

    List<User> findByClientSegment(ClientSegment clientSegmentId);
}
