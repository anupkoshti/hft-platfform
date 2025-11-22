package org.pm.orderexecution.consumer;

import org.pm.common.model.ValidatedTrade;
import org.pm.orderexecution.service.OrderExecutionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ValidatedTradesConsumer {

    private final OrderExecutionService orderExecutionService;

    public ValidatedTradesConsumer(OrderExecutionService orderExecutionService) {
        this.orderExecutionService = orderExecutionService;
    }

    @KafkaListener(topics = "${topics.validated-trades}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ValidatedTrade validatedTrade) {
        System.out.println("📥 Received validated trade: " + validatedTrade);
        
        // Only execute if trade is valid
        if (validatedTrade.isValid()) {
            orderExecutionService.executeOrder(validatedTrade);
        } else {
            System.out.println("⏭️  Skipping invalid trade: " + validatedTrade.getReason());
        }
    }
}