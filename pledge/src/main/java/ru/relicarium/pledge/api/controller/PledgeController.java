package ru.relicarium.pledge.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.relicarium.pledge.api.mapper.PledgeApiMapper;
import ru.relicarium.pledge.application.dto.AcceptPledgeCommand;
import ru.relicarium.pledge.application.dto.ApplyPledgeTransitionCommand;
import ru.relicarium.pledge.application.dto.DisbursePledgeCommand;
import ru.relicarium.pledge.application.dto.PayInterestPledgeCommand;
import ru.relicarium.pledge.application.dto.RedeemPledgeCommand;
import ru.relicarium.pledge.application.dto.request.ApplyPledgeTransitionRequest;
import ru.relicarium.pledge.application.dto.request.CreatePledgeRequest;
import ru.relicarium.pledge.application.dto.request.DisbursePledgeRequest;
import ru.relicarium.pledge.application.dto.request.PayInterestPledgeRequest;
import ru.relicarium.pledge.application.dto.request.RedeemPledgeRequest;
import ru.relicarium.pledge.application.dto.response.PledgeResponse;
import ru.relicarium.pledge.application.service.PledgeAcceptanceService;
import ru.relicarium.pledge.application.service.PledgeDisbursementService;
import ru.relicarium.pledge.application.service.PledgeInterestPaymentService;
import ru.relicarium.pledge.application.service.PledgeRedemptionService;
import ru.relicarium.pledge.application.service.PledgeTransitionService;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pledges")
public class PledgeController {

    private final PledgeAcceptanceService pledgeAcceptanceService;
    private final PledgeApiMapper pledgeApiMapper;
    private final PledgeDisbursementService pledgeDisbursementService;
    private final PledgeRedemptionService pledgeRedemptionService;
    private final PledgeInterestPaymentService pledgeInterestPaymentService;
    private final PledgeTransitionService pledgeTransitionService;

    public PledgeController(PledgeAcceptanceService pledgeAcceptanceService,
                            PledgeApiMapper pledgeApiMapper,
                            PledgeDisbursementService pledgeDisbursementService,
                            PledgeRedemptionService pledgeRedemptionService,
                            PledgeInterestPaymentService pledgeInterestPaymentService,
                            PledgeTransitionService pledgeTransitionService) {

        this.pledgeAcceptanceService = pledgeAcceptanceService;
        this.pledgeApiMapper = pledgeApiMapper;
        this.pledgeDisbursementService = pledgeDisbursementService;
        this.pledgeRedemptionService = pledgeRedemptionService;
        this.pledgeInterestPaymentService = pledgeInterestPaymentService;
        this.pledgeTransitionService = pledgeTransitionService;
    }

    @PostMapping
    public ResponseEntity<PledgeResponse> accept(
            @Valid @RequestBody CreatePledgeRequest pledgeRequest) {

        AcceptPledgeCommand command = pledgeApiMapper.toCommand(pledgeRequest);
        Pledge pledge = pledgeAcceptanceService.acceptPledge(command);
        PledgeResponse response = pledgeApiMapper.toResponse(pledge);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{pledgeId}/disbursements")
    public ResponseEntity<PledgeResponse> disburse(
            @PathVariable("pledgeId") UUID pledgeId,
            @Valid @RequestBody DisbursePledgeRequest pledgeRequest) {

        DisbursePledgeCommand pledgeCommand = pledgeApiMapper.toDisburseCommand(pledgeRequest);
        Pledge pledge = pledgeDisbursementService.disburse(pledgeId, pledgeCommand);

        return ResponseEntity.ok(pledgeApiMapper.toResponse(pledge));
    }

    @PostMapping("/{pledgeId}/redemptions")
    public ResponseEntity<PledgeResponse> redeem(
            @PathVariable("pledgeId") UUID pledgeId,
            @Valid @RequestBody RedeemPledgeRequest pledgeRequest) {

        RedeemPledgeCommand command = pledgeApiMapper.toRedeemCommand(pledgeRequest);
        Pledge pledge = pledgeRedemptionService.redeem(pledgeId, command);

        return ResponseEntity.ok(pledgeApiMapper.toResponse(pledge));
    }

    @PostMapping("/{pledgeId}/interest-payments")
    public ResponseEntity<PledgeResponse> interestPayment(
            @PathVariable("pledgeId") UUID pledgeId,
            @Valid @RequestBody PayInterestPledgeRequest request) {
        PayInterestPledgeCommand command = pledgeApiMapper.toPayInterestCommand(request);
        Pledge pledge = pledgeInterestPaymentService.payInterest(pledgeId, command);

        return ResponseEntity.ok(pledgeApiMapper.toResponse(pledge));
    }

    @PostMapping("/{pledgeId}/status-transitions")
    public ResponseEntity<PledgeResponse> statusTransition(
            @PathVariable("pledgeId") UUID pledgeId,
            @Valid @RequestBody ApplyPledgeTransitionRequest request) {
        ApplyPledgeTransitionCommand command = pledgeApiMapper.toApplyPledgeTransitionCommand(request);
        Pledge pledge = pledgeTransitionService.apply(pledgeId, command);

        return ResponseEntity.ok(pledgeApiMapper.toResponse(pledge));
    }
}
