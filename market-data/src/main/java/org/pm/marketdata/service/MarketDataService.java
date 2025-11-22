package org.pm.marketdata.service;


import org.pm.common.model.Tick;
import org.pm.marketdata.util.JsonUtil;
import org.pm.marketdata.websocket.BinanceWebSocketClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class MarketDataService {

    @Value("${marketdata.websocket-url}")
    private String websocketUrl;

    @Value("${marketdata.topic}")
    private String kafkaTopic;

    private final KafkaTemplate<String, Tick> kafkaTemplate;
    private final BinanceWebSocketClient wsClient;

    //Listen to live market data from WebSocket
    // → convert it to Tick object → send it to Kafka.

    public MarketDataService(KafkaTemplate<String, Tick> kafkaTemplate,
                             BinanceWebSocketClient wsClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.wsClient = wsClient;
    }

    //WebSocketClient, connect to Binance WebSocket
    // ,and whenever you get a message, call my handleMessage method.
    public void startStreaming() {
        wsClient.connect(websocketUrl, this::handleMessage);
    }

    private void handleMessage(String msg) {
        try {
            JsonNode json = JsonUtil.parse(msg);

            Tick tick = new Tick();
//            The Binance message looks like:
//            {
//                "s": "BTCUSDT",
//                    "p": "88500.50",
//                    "q": "0.002",
//                    "T": 1712345678912
//            }

            tick.setSymbol(json.get("s").asText());
            tick.setPrice(json.get("p").asDouble());
            tick.setQuantity(json.get("q").asDouble());
            tick.setTimestamp(json.get("T").asLong());

//            This creates a Java object:
//
//            Tick {
//                symbol = "BTCUSDT"
//                price = 88500.50
//                quantity = 0.002
//                timestamp = 1712345678912
//            }
            System.out.println("Sending tick to Kafka: " + tick.getSymbol() + " @ " + tick.getPrice());
            kafkaTemplate.send(kafkaTopic, tick.getSymbol(), tick)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            System.err.println("Failed to send message: " + ex.getMessage());
                        } else {
                            System.out.println("Message sent successfully to partition: " + result.getRecordMetadata().partition());
                        }
                    });

        } catch (Exception e) {
            System.err.println("Error parsing tick: " + msg);
            e.printStackTrace();
        }
    }
}
