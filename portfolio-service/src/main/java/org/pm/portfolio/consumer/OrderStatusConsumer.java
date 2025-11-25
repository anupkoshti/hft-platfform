package org.pm.portfolio.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pm.portfolio.service.PortfolioService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStatusConsumer {

    private final PortfolioService portfolioService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "order-status", groupId = "portfolio-service-group")
    public void consumeOrderStatus(Map<String, Object> message) {
        try {
            log.info("Received order status: {}", message);
            
            String tradeId = (String) message.get("tradeId");
            String brokerOrderId = (String) message.get("brokerOrderId");
            String symbol = (String) message.get("symbol");
            String side = (String) message.get("side");
            Double price = getDoubleValue(message.get("price"));
            String status = (String) message.get("status");

            portfolioService.processOrderStatus(tradeId, brokerOrderId, symbol, side, price, status);
            
        } catch (Exception e) {
            log.error("Error processing order status: {}", e.getMessage(), e);
        }
    }

    private Double getDoubleValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
}
