package org.pm.orderexecution.producer;

import org.pm.orderexecution.model.OrderStatusEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topics.order-status}")
    private String orderStatusTopic;

    public OrderStatusProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(OrderStatusEvent event) {
        kafkaTemplate.send(orderStatusTopic, event.getTradeId(), event);
        System.out.println("Published order-status for tradeId=" + event.getTradeId() + " status=" + event.getStatus());
    }
}