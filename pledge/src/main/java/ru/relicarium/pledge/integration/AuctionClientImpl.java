package ru.relicarium.pledge.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import ru.relicarium.pledge.integration.dto.AuctionCompleteLotRequest;
import ru.relicarium.pledge.integration.dto.AuctionLotResponse;
import ru.relicarium.pledge.integration.dto.AuctionRegisterLotRequest;

@Service
@RequiredArgsConstructor
public class AuctionClientImpl implements AuctionClient{

    private final RestClient auctionRestClient;

    @Override
    public AuctionLotResponse registerLot(AuctionRegisterLotRequest httpRequest) {

        return auctionRestClient
                .post()
                .uri("/api/v1/lots")
                .contentType(MediaType.APPLICATION_JSON)
                .body(httpRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new AuctionIntegrationException("Auction lot register failed, status ="
                            + response.getStatusCode());
                })
                .body(AuctionLotResponse.class);
    }

    @Override
    @Transactional
    public AuctionLotResponse completeLot(AuctionCompleteLotRequest httpRequest) {

        return auctionRestClient
                .post()
                .uri("api/v1/lots/completions")
                .body(httpRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new AuctionIntegrationException("Auction lot complete failed, status ="
                            + response.getStatusCode());
                })
                .body(AuctionLotResponse.class);

    }
}
