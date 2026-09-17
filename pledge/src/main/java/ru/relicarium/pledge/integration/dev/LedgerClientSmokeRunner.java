package ru.relicarium.pledge.integration.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.relicarium.pledge.integration.LedgerClient;
import ru.relicarium.pledge.integration.dto.LedgerAmountDueResponse;
import ru.relicarium.pledge.integration.dto.LedgerDisburseRequest;
import ru.relicarium.pledge.integration.dto.LedgerLoanResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@Slf4j
@Profile("dev")
@RequiredArgsConstructor
public class LedgerClientSmokeRunner implements CommandLineRunner {

    private final LedgerClient ledgerClient;

    @Override
    public void run(String... args) throws Exception {

        log.info("Test start");

        UUID pledgeId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        String operationId = "smoke-disb-" + pledgeId;

        LedgerDisburseRequest request = new LedgerDisburseRequest(
                pledgeId,
                clientId,
                new BigDecimal("30000.00"),
                new BigDecimal("0.1200"),
                OffsetDateTime.now(),
                LocalDate.now().plusDays(30),
                operationId
        );

        LedgerLoanResponse loanResponse = ledgerClient.disburse(request);
        log.info("disburse OK: pledgeId={}, closedAt={}", loanResponse.pledgeId(), loanResponse.closedAt());

        LedgerAmountDueResponse due = ledgerClient.getAmount(pledgeId, LocalDate.now());
        log.info("amount due OK: totalDue={}, interestDue={}, closed={}",
                due.totalDue(), due.interestDue(), due.closed());
        log.info("=== Ledger smoke: OK ===");

    }
}
