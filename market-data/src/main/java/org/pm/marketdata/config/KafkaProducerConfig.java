package org.pm.marketdata.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.pm.common.model.Tick;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Tick> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

//        “Convert the key into bytes using StringSerializer.”
//        You send "BTCUSDT" as key → Kafka expects bytes → StringSerializer handles this.
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

//        “Convert the Tick object into JSON before sending it to Kafka.”
//        Kafka stores the JSON.
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Tick> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
