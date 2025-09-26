package fr.poc.hackaton.service;

import fr.poc.hackaton.business.dto.BankAccount;
import fr.poc.hackaton.business.dto.User;
import fr.poc.hackaton.business.dto.Transaction;
import fr.poc.hackaton.business.dto.User;
import fr.poc.hackaton.business.usecase.CreateUser;
import fr.poc.hackaton.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final CreateUser createUser;
    private final UserRepository userRepository;

    public synchronized User getUser(String userId, BankAccount bankAccount) {
        User user = this.userRepository.findByUserId(userId);
        if(user == null) {
            user = this.userRepository.save(this.createUser.createUser(userId, bankAccount));
            System.out.println("Creating new user : " + userId);
        }
        return user;
    }
}
