package com.example.usermanagement.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PaymentGatewayHealthIndicator implements HealthIndicator {

    private final Random random = new Random();

    @Override
    public Health health() {
        boolean isUp = random.nextBoolean();
        if (isUp) {
            return Health.up().withDetail("paymentGateway", "UP").build();
        } else {
            return Health.down().withDetail("paymentGateway", "DOWN").build();
        }
    }
}