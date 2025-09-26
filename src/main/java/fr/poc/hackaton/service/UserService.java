package fr.poc.hackaton.service;

import fr.poc.hackaton.business.dto.*;
import fr.poc.hackaton.business.dto.User;
import fr.poc.hackaton.business.usecase.CreateUser;
import fr.poc.hackaton.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public User getUserById(String id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public List<User> getBySegment(ClientSegment segment){
        return this.userRepository.findByClientSegment(segment);
    }

    public void updateUser(User user) {
        this.userRepository.save(user);
    }
}
