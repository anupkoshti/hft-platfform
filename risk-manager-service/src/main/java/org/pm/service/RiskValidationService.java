package org.pm.service;

import org.pm.common.model.TradeSignal;
import org.pm.model.ValidatedTrade;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RiskValidationService {

    // Risk limits
    private static final double MAX_PRICE_PER_UNIT = 150000;     // Max $150k per unit (for BTC)
    private static final double MIN_PRICE_PER_UNIT = 100;        // Min $100 per unit
    private static final double MAX_POSITION_SIZE = 1000000;     // Max $1M total position
    private static final int MAX_TRADES_PER_MINUTE = 1000;       // Rate limiting (increased for high-frequency)
    
    // Track positions, trade frequency, and last signal
    private final Map<String, Double> positions = new ConcurrentHashMap<>();
    private final Map<String, TradeCounter> tradeCounters = new ConcurrentHashMap<>();
    private final Map<String, String> lastSignals = new ConcurrentHashMap<>();

    public ValidatedTrade validate(TradeSignal signal) {
        System.out.println("Validating signal: " + signal);
        
        // Skip validation for HOLD signals
        if ("HOLD".equals(signal.getSignal())) {
            return ValidatedTrade.builder()
                    .signal(signal)
                    .valid(true)
                    .reason("HOLD signal - no action required")
                    .build();
        }

        // Check if this is a duplicate signal (same as last one)
        String lastSignal = lastSignals.get(signal.getSymbol());
        if (signal.getSignal().equals(lastSignal)) {
            return ValidatedTrade.builder()
                    .signal(signal)
                    .valid(false)
                    .reason("Duplicate signal - already " + signal.getSignal())
                    .build();
        }

        // 1. Validate price is reasonable
        if (signal.getPrice() <= 0) {
            return invalid(signal, "Invalid price: must be positive");
        }

        if (signal.getPrice() < MIN_PRICE_PER_UNIT) {
            return invalid(signal, "Price too low: $" + signal.getPrice() + " < $" + MIN_PRICE_PER_UNIT);
        }

        if (signal.getPrice() > MAX_PRICE_PER_UNIT) {
            return invalid(signal, "Price too high: $" + signal.getPrice() + " > $" + MAX_PRICE_PER_UNIT);
        }

        // 2. Check rate limiting
        TradeCounter counter = tradeCounters.computeIfAbsent(
            signal.getSymbol(), 
            k -> new TradeCounter()
        );
        
        if (!counter.allowTrade()) {
            return invalid(signal, "Rate limit exceeded: max " + MAX_TRADES_PER_MINUTE + " trades/minute");
        }

        // 3. Check position limits
        double currentPosition = positions.getOrDefault(signal.getSymbol(), 0.0);
        double orderValue = signal.getPrice(); // Assuming 1 unit per trade
        
        double newPosition;
        if ("BUY".equals(signal.getSignal())) {
            newPosition = currentPosition + orderValue;
        } else { // SELL
            newPosition = currentPosition - orderValue;
        }

        if (Math.abs(newPosition) > MAX_POSITION_SIZE) {
            return invalid(signal, 
                String.format("Position limit exceeded: $%.2f > $%.2f", 
                    Math.abs(newPosition), MAX_POSITION_SIZE));
        }

        // Update position and last signal (in real system, this would be done after execution)
        positions.put(signal.getSymbol(), newPosition);
        lastSignals.put(signal.getSymbol(), signal.getSignal());
        
        System.out.println(String.format("✓ Trade validated: %s %s @ $%.2f | Position: $%.2f", 
            signal.getSignal(), signal.getSymbol(), signal.getPrice(), newPosition));

        return ValidatedTrade.builder()
                .signal(signal)
                .valid(true)
                .reason(String.format("OK - Position: $%.2f", newPosition))
                .build();
    }

    private ValidatedTrade invalid(TradeSignal signal, String reason) {
        System.out.println("✗ Trade rejected: " + reason);
        return ValidatedTrade.builder()
                .signal(signal)
                .valid(false)
                .reason(reason)
                .build();
    }

    // Simple rate limiter
    private static class TradeCounter {
        private long windowStart = System.currentTimeMillis();
        private int count = 0;

        synchronized boolean allowTrade() {
            long now = System.currentTimeMillis();
            
            // Reset counter every minute
            if (now - windowStart > 60000) {
                windowStart = now;
                count = 0;
            }

            if (count >= MAX_TRADES_PER_MINUTE) {
                return false;
            }

            count++;
            return true;
        }
    }
}
