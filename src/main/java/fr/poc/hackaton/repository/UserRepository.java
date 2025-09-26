package fr.poc.hackaton.repository;


import fr.poc.hackaton.business.dto.Transaction;
import fr.poc.hackaton.business.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUserId(String id);

}
