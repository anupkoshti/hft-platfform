package org.pm.consumer;

import org.pm.common.model.TradeSignal;
import org.pm.producer.ValidatedTradeProducer;
import org.pm.service.RiskValidationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TradeSignalConsumer {

    private final RiskValidationService validationService;
    private final ValidatedTradeProducer producer;

    public TradeSignalConsumer(RiskValidationService validationService, ValidatedTradeProducer producer) {
        this.validationService = validationService;
        this.producer = producer;
    }

    @KafkaListener(topics = "${topics.trade-signals}", groupId = "risk-manager-group")
    public void onMessage(TradeSignal signal) {
        System.out.println("Received trade signal: " + signal);

        var validated = validationService.validate(signal);

        producer.publish(validated);

        System.out.println("Validation result published: " + validated);
    }
}
