package ru.relicarium.pledge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.relicarium.pledge.integration.config.LedgerProperties;

@SpringBootApplication
@EnableConfigurationProperties(LedgerProperties.class)
public class PledgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PledgeApplication.class, args);
    }
}
