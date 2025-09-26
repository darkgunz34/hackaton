package fr.poc.hackaton.controler;

import fr.poc.hackaton.business.dto.User;
import fr.poc.hackaton.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ClientController {

    private final OrchestratorService orchestratorService;

    @GetMapping("/{id}/operations")
    public Object getOperationsByDateRange(
            @PathVariable String id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return this.orchestratorService.findOperationsByUserAndDateRange(id, startDate, endDate);
    }
}
