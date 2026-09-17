package ru.relicarium.pledge.integration;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.relicarium.pledge.integration.dto.LedgerAmountDueResponse;
import ru.relicarium.pledge.integration.dto.LedgerDisburseRequest;
import ru.relicarium.pledge.integration.dto.LedgerLoanResponse;
import ru.relicarium.pledge.integration.dto.LedgerPayInterestRequest;
import ru.relicarium.pledge.integration.dto.LedgerRepayRequest;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class LedgerClientImpl implements  LedgerClient{

    private final RestClient ledgerRestClient;

    public LedgerClientImpl(RestClient restClient) {

        this.ledgerRestClient = restClient;
    }

    @Override
    public LedgerLoanResponse disburse(LedgerDisburseRequest httpRequest) {
        return ledgerRestClient.post()
                .uri("/api/v1/loans/disbursements")
                .contentType(MediaType.APPLICATION_JSON)
                .body(httpRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new LedgerIntegrationException("Ledger disburse failed, status=" + response.getStatusCode());
                })
                .body(LedgerLoanResponse.class);
    }

    @Override
    public LedgerLoanResponse payInterest(LedgerPayInterestRequest httpRequest) {
        return ledgerRestClient.post()
                .uri("/api/v1/loans/interest-payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(httpRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new LedgerIntegrationException("Ledger interest-payments failed, status=" + response.getStatusCode());
                })
                .body(LedgerLoanResponse.class);
    }

    @Override
    public LedgerLoanResponse repay(LedgerRepayRequest httpRequest) {
        return ledgerRestClient.post()
                .uri("/api/v1/loans/repayments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(httpRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new LedgerIntegrationException("Ledger repayments failed, status=" + response.getStatusCode());
                })
                .body(LedgerLoanResponse.class);
    }

    @Override
    public LedgerAmountDueResponse getAmount(UUID pledgeId, LocalDate calculationDate) {
        return ledgerRestClient.get()
                .uri(uriBuilder -> {
                    var builder  = uriBuilder
                            .path("/api/v1/loans/amount-due")
                            .queryParam("pledgeId", pledgeId);
                    if (calculationDate != null) {
                        builder.queryParam("asOf", calculationDate);
                    }
                    return builder.build();
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new LedgerIntegrationException("Ledger amount-due failed status=" + response.getStatusCode());
                })
                .body(LedgerAmountDueResponse.class);
    }
}
