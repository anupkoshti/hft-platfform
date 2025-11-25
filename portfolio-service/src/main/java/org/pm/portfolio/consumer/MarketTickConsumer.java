package org.pm.portfolio.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pm.portfolio.service.PortfolioService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarketTickConsumer {

    private final PortfolioService portfolioService;

    @KafkaListener(topics = "market-ticks", groupId = "portfolio-service-group")
    public void consumeMarketTick(Map<String, Object> message) {
        try {
            String symbol = (String) message.get("symbol");
            Double price = getDoubleValue(message.get("price"));
            
            portfolioService.updateMarketPrice(symbol, price);
            
        } catch (Exception e) {
            log.error("Error processing market tick: {}", e.getMessage(), e);
        }
    }

    private Double getDoubleValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
}
