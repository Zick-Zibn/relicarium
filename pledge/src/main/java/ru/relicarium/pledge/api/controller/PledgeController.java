package ru.relicarium.pledge.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.relicarium.pledge.api.mapper.PledgeApiMapper;
import ru.relicarium.pledge.application.dto.AcceptPledgeCommand;
import ru.relicarium.pledge.application.dto.request.CreatePledgeRequest;
import ru.relicarium.pledge.application.dto.response.PledgeResponse;
import ru.relicarium.pledge.application.service.PledgeAcceptanceService;
import ru.relicarium.pledge.domain.model.Pledge;

@RestController
@RequestMapping("/api/v1/pledges")
public class PledgeController {

    private final PledgeAcceptanceService pledgeAcceptanceService;
    private final PledgeApiMapper pledgeApiMapper;

    public PledgeController(PledgeAcceptanceService pledgeAcceptanceService,
                            PledgeApiMapper pledgeApiMapper) {
        this.pledgeAcceptanceService = pledgeAcceptanceService;
        this.pledgeApiMapper = pledgeApiMapper;
    }

    @PostMapping
    public ResponseEntity<PledgeResponse> accept(
            @Valid @RequestBody CreatePledgeRequest pledgeRequest) {
        AcceptPledgeCommand command = pledgeApiMapper.toCommand(pledgeRequest);
        Pledge pledge = pledgeAcceptanceService.acceptPledge(command);
        PledgeResponse response = pledgeApiMapper.toResponse(pledge);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
