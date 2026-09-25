package ru.relicarium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.relicarium.auction.security.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class AuctionApplication {

    public static void main(String[] args) {

        SpringApplication.run(AuctionApplication.class, args);
    }
}
