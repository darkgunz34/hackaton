package fr.poc.hackaton.service;

import fr.poc.hackaton.business.dto.BankAccount;
import fr.poc.hackaton.business.dto.ClientSegment;
import fr.poc.hackaton.business.dto.User;
import fr.poc.hackaton.business.usecase.CreateUser;
import fr.poc.hackaton.repository.ClientSegmentRepository;
import fr.poc.hackaton.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientSegmentService {

    private final ClientSegmentRepository clientSegmentRepository;

    public synchronized ClientSegment getClientSegment(String id) {
        ClientSegment clientSegment = clientSegmentRepository.findById(id).orElse(null);
        if(clientSegment == null) {
            clientSegment = new ClientSegment();
            clientSegment.setName("VIP");
            clientSegment.setId("1");
            clientSegment = this.clientSegmentRepository.save(clientSegment);
        }
        return clientSegment;
    }
}
