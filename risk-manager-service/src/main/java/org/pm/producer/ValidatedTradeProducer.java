package org.pm.producer;

import org.pm.common.model.ValidatedTrade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ValidatedTradeProducer {

    private final KafkaTemplate<String, ValidatedTrade> kafkaTemplate;

    @Value("${topics.validated-trades}")
    private String validatedTopic;

    public ValidatedTradeProducer(KafkaTemplate<String, ValidatedTrade> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ValidatedTrade trade) {
        kafkaTemplate.send(validatedTopic, trade.getSignal().getSymbol(), trade)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("Failed to send validated trade: " + ex.getMessage());
                    } else {
                        System.out.println("✓ Validated trade sent to partition " + 
                                result.getRecordMetadata().partition() + ": " + trade);
                    }
                });
    }
}
