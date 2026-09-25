package ru.relicarium.pledge.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.relicarium.pledge.api.mapper.PledgeApiMapper;
import ru.relicarium.pledge.application.dto.AcceptPledgeCommand;
import ru.relicarium.pledge.application.dto.ApplyPledgeTransitionCommand;
import ru.relicarium.pledge.application.dto.AuctionReturnPledgeCommand;
import ru.relicarium.pledge.application.dto.AuctionSalePledgeCommand;
import ru.relicarium.pledge.application.dto.DisbursePledgeCommand;
import ru.relicarium.pledge.application.dto.PayClientSurplusPledgeCommand;
import ru.relicarium.pledge.application.dto.PayInterestPledgeCommand;
import ru.relicarium.pledge.application.dto.RedeemPledgeCommand;
import ru.relicarium.pledge.application.dto.SendToAuctionPledgeCommand;
import ru.relicarium.pledge.application.dto.request.ApplyPledgeTransitionRequest;
import ru.relicarium.pledge.application.dto.request.AuctionReturnPledgeRequest;
import ru.relicarium.pledge.application.dto.request.AuctionSalePledgeRequest;
import ru.relicarium.pledge.application.dto.request.CreatePledgeRequest;
import ru.relicarium.pledge.application.dto.request.DisbursePledgeRequest;
import ru.relicarium.pledge.application.dto.request.PayClientSurplusPledgeRequest;
import ru.relicarium.pledge.application.dto.request.PayInterestPledgeRequest;
import ru.relicarium.pledge.application.dto.request.RedeemPledgeRequest;
import ru.relicarium.pledge.application.dto.request.SendToAuctionPledgeRequest;
import ru.relicarium.pledge.application.dto.response.PledgeAmountDueResponse;
import ru.relicarium.pledge.application.dto.response.PledgePageResponse;
import ru.relicarium.pledge.application.dto.response.PledgeResponse;
import ru.relicarium.pledge.application.service.PledgeAcceptanceService;
import ru.relicarium.pledge.application.service.PledgeAmountDueService;
import ru.relicarium.pledge.application.service.PledgeAuctionReturnService;
import ru.relicarium.pledge.application.service.PledgeAuctionSaleService;
import ru.relicarium.pledge.application.service.PledgeAuctionSubmissionService;
import ru.relicarium.pledge.application.service.PledgeClientSurplusPayoutService;
import ru.relicarium.pledge.application.service.PledgeDisbursementService;
import ru.relicarium.pledge.application.service.PledgeInterestPaymentService;
import ru.relicarium.pledge.application.service.PledgeQueryService;
import ru.relicarium.pledge.application.service.PledgeRedemptionService;
import ru.relicarium.pledge.application.service.PledgeTransitionService;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.model.Pledge;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pledges")
public class PledgeController {

    private final PledgeAcceptanceService pledgeAcceptanceService;
    private final PledgeApiMapper pledgeApiMapper;
    private final PledgeDisbursementService pledgeDisbursementService;
    private final PledgeRedemptionService pledgeRedemptionService;
    private final PledgeInterestPaymentService pledgeInterestPaymentService;
    private final PledgeTransitionService pledgeTransitionService;
    private final PledgeAuctionSubmissionService pledgeAuctionSubmissionService;
    private final PledgeAuctionSaleService pledgeAuctionSaleService;
    private final PledgeAuctionReturnService pledgeAuctionReturnService;
    private final PledgeQueryService pledgeQueryService;
    private final PledgeAmountDueService pledgeAmountDueService;
    private final PledgeClientSurplusPayoutService pledgeClientSurplusPayoutService;

    @GetMapping
    public ResponseEntity<PledgePageResponse> listPledges(
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "status", required = false) PledgeStatus status,
            @PageableDefault(size = 20, sort = "acceptedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        PledgePageResponse response = pledgeQueryService.listPledges(phone, status, pageable);
        return ResponseEntity.ok(response);
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

    @PostMapping("/{pledgeId}/auction-submissions")
    public ResponseEntity<PledgeResponse> submitToAuction(
            @PathVariable("pledgeId") UUID pledgeId,
            @Valid @RequestBody SendToAuctionPledgeRequest request) {

        SendToAuctionPledgeCommand command = pledgeApiMapper.toSendToAuctionPledgeCommand(request);
        Pledge pledge = pledgeAuctionSubmissionService.submit(pledgeId, command);

        return ResponseEntity.ok(pledgeApiMapper.toResponse(pledge));
    }

    @PostMapping("/{pledgeId}/auction-sales")
    public ResponseEntity<PledgeResponse> salesAuction(
            @PathVariable("pledgeId") UUID pledgeId,
            @Valid @RequestBody AuctionSalePledgeRequest request) {

        AuctionSalePledgeCommand command = pledgeApiMapper.toAuctionSalePledgeCommand(request);
        Pledge pledge = pledgeAuctionSaleService.saleLot(pledgeId, command);

        return ResponseEntity.ok(pledgeApiMapper.toResponse(pledge));
    }

    @PostMapping("/{pledgeId}/auction-returns")
    public ResponseEntity<PledgeResponse> returnAuction(
            @PathVariable("pledgeId") UUID pledgeId,
            @Valid @RequestBody AuctionReturnPledgeRequest request) {

        AuctionReturnPledgeCommand command = pledgeApiMapper.toAuctionReturnPledgeCommand(request);
        Pledge pledge = pledgeAuctionReturnService.returnLot(pledgeId, command);

        return ResponseEntity.ok(pledgeApiMapper.toResponse(pledge));
    }

    @GetMapping("/{pledgeId}")
    public ResponseEntity<PledgeResponse> getById(@PathVariable("pledgeId") UUID pledgeId) {

        Pledge pledge = pledgeQueryService.getById(pledgeId);

        return ResponseEntity.ok(pledgeApiMapper.toResponse(pledge));
    }

    @GetMapping("/{pledgeId}/amount-due")
    public ResponseEntity<PledgeAmountDueResponse> getAmountDue(
            @PathVariable("pledgeId") UUID pledgeId,
            @RequestParam(value = "asOf", required = false) LocalDate asOf) {

        return ResponseEntity.ok(pledgeAmountDueService.getAmountDue(pledgeId, asOf));
    }

    @PostMapping("/{pledgeId}/client-payouts")
    public ResponseEntity<PledgeResponse> clientPayouts(
            @PathVariable("pledgeId") UUID pledgeId,
            @Valid @RequestBody PayClientSurplusPledgeRequest request) {

        PayClientSurplusPledgeCommand command = pledgeApiMapper.toPayClientSurplusPledgeCommand(request);
        Pledge pledge = pledgeClientSurplusPayoutService.payClientSurplus(pledgeId, command);

        return ResponseEntity.ok(pledgeApiMapper.toResponse(pledge));
    }
}
