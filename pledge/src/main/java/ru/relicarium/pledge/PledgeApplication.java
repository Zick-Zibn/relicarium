package ru.relicarium.pledge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.relicarium.pledge.integration.config.AuctionProperties;
import ru.relicarium.pledge.integration.config.LedgerProperties;
import ru.relicarium.pledge.security.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties({LedgerProperties.class, AuctionProperties.class, JwtProperties.class})
public class PledgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PledgeApplication.class, args);
    }
}
