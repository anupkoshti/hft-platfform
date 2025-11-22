package org.pm.orderexecution.broker;

import org.pm.common.model.ValidatedTrade;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

/**
 * Mock Broker Service - Simulates a real broker API for testing
 * In production, replace this with actual broker integration (Zerodha, Binance, etc.)
 */
@Component
public class MockBrokerService {

    private final Random random = new Random();

    /**
     * Simulates placing an order with a broker
     * Returns a Mono that completes after a realistic delay
     */
    public Mono<BrokerOrderResponse> placeOrder(ValidatedTrade validatedTrade, String tradeId) {
        System.out.println("📤 Mock Broker: Received order request for " + validatedTrade.getSignal().getSymbol() + 
                          " " + validatedTrade.getSignal().getSignal() + " @ $" + validatedTrade.getSignal().getPrice());

        // Simulate network latency (50-200ms)
        int latencyMs = 50 + random.nextInt(150);

        // Simulate 95% success rate
        boolean success = random.nextDouble() < 0.95;

        return Mono.delay(Duration.ofMillis(latencyMs))
                .map(tick -> {
                    if (success) {
                        BrokerOrderResponse response = new BrokerOrderResponse();
                        response.orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
                        response.status = "FILLED";
                        response.timestamp = System.currentTimeMillis();
                        response.filledPrice = validatedTrade.getSignal().getPrice() + (random.nextDouble() - 0.5) * 0.01; // Small slippage
                        response.filledQuantity = 1.0; // Default quantity
                        
                        System.out.println("✅ Mock Broker: Order FILLED - " + response.orderId + 
                                          " @ $" + String.format("%.2f", response.filledPrice));
                        return response;
                    } else {
                        // Simulate rejection
                        throw new RuntimeException("Insufficient margin");
                    }
                });
    }

    /**
     * Response from mock broker
     */
    public static class BrokerOrderResponse {
        public String orderId;
        public String status;
        public long timestamp;
        public double filledPrice;
        public double filledQuantity;
    }
}
