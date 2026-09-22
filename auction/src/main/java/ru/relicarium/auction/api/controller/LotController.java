package ru.relicarium.auction.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.relicarium.auction.api.mapper.AuctionApiMapper;
import ru.relicarium.auction.application.dto.CompleteLotCommand;
import ru.relicarium.auction.application.dto.CreateLotCommand;
import ru.relicarium.auction.application.dto.request.CompleteLotRequest;
import ru.relicarium.auction.application.dto.request.CreateLotRequest;
import ru.relicarium.auction.application.dto.responce.LotResponse;
import ru.relicarium.auction.application.service.LotCompletionService;
import ru.relicarium.auction.application.service.LotRegistrationService;
import ru.relicarium.auction.domain.model.Lot;

@RestController
@RequestMapping("/api/v1/lots")
@RequiredArgsConstructor
public class LotController {

    private final LotRegistrationService lotRegistrationService;
    private final LotCompletionService lotCompletionService;
    private final AuctionApiMapper mapper;

    @PostMapping()
    public ResponseEntity<LotResponse> register(@Valid @RequestBody CreateLotRequest request) {
        CreateLotCommand command = mapper.toCreateLotCommand(request);
        Lot lot = lotRegistrationService.register(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toLotResponse(lot));
    }

    @PostMapping("/completions")
    public ResponseEntity<LotResponse> complete(@Valid @RequestBody CompleteLotRequest request) {

        CompleteLotCommand command = mapper.toCompleteLotCommand(request);
        Lot lot = lotCompletionService.complete(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toLotResponse(lot));
    }
}
