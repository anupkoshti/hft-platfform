package org.pm.portfolio.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pm.portfolio.model.PortfolioUpdate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortfolioUpdateProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "portfolio-updates";

    public void publishPortfolioUpdate(PortfolioUpdate update) {
        try {
            kafkaTemplate.send(TOPIC, update);
            log.info("Published portfolio update: Total P&L = {}", update.getTotalPnL());
        } catch (Exception e) {
            log.error("Error publishing portfolio update: {}", e.getMessage(), e);
        }
    }
}
