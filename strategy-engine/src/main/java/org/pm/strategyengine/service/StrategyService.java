package org.pm.strategyengine.service;


import org.pm.common.model.Tick;
import org.pm.common.model.TradeSignal;
import org.pm.strategyengine.utils.SMAUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class StrategyService {

    private final KafkaTemplate<String, TradeSignal> kafkaTemplate;

    @Value("${topics.signals}")
    private String signalsTopic;

    @Value("${topics.market}")
    private String marketTopic;

    // thread-safe maps for per-symbol price windows
    private final Map<String, Deque<Double>> shortWindow = new ConcurrentHashMap<>();
    private final Map<String, Deque<Double>> longWindow = new ConcurrentHashMap<>();

    private static final int SHORT_PERIOD = 5;
    private static final int LONG_PERIOD = 10;

    public StrategyService(KafkaTemplate<String, TradeSignal> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        System.out.println("✓ StrategyService bean created!");
    }

    @KafkaListener(topics = "market-ticks", groupId = "strategy-engine")
    public void onMessage(Tick tick) {
        System.out.println("Received tick from Kafka: " + tick);
        
        if (tick == null || tick.getSymbol() == null) {
            System.err.println("Invalid tick received: " + tick);
            return;
        }

        updateWindow(shortWindow, tick.getSymbol(), tick.getPrice(), SHORT_PERIOD);
        updateWindow(longWindow, tick.getSymbol(), tick.getPrice(), LONG_PERIOD);

        Deque<Double> s = shortWindow.get(tick.getSymbol());
        Deque<Double> l = longWindow.get(tick.getSymbol());

        if (s == null || l == null || s.size() < SHORT_PERIOD || l.size() < LONG_PERIOD) {
            System.out.println("Not enough data yet - short: " + (s != null ? s.size() : 0) + 
                             ", long: " + (l != null ? l.size() : 0));
            return;
        }

        double smaShort = SMAUtils.calculateSMA(s);
        double smaLong = SMAUtils.calculateSMA(l);

        String signal;
        if (smaShort > smaLong) signal = "BUY";
        else if (smaShort < smaLong) signal = "SELL";
        else signal = "HOLD";

        TradeSignal tradeSignal = new TradeSignal(tick.getSymbol(), signal, tick.getPrice(), tick.getTimestamp());

        // Validate signal before sending
        if (tradeSignal == null || tradeSignal.getSymbol() == null || tradeSignal.getSignal() == null) {
            System.err.println("Invalid trade signal created: " + tradeSignal);
            return;
        }

        System.out.printf("Sending signal to Kafka topic '%s': %s%n", signalsTopic, tradeSignal);
        
        kafkaTemplate.send(signalsTopic, tick.getSymbol(), tradeSignal)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("Failed to send trade signal: " + ex.getMessage());
                    } else {
                        System.out.printf("✓ Signal sent successfully to partition %d | %s for %s @ %.6f%n",
                                result.getRecordMetadata().partition(), signal, tick.getSymbol(), tick.getPrice());
                    }
                });
    }

    private void updateWindow(Map<String, Deque<Double>> map, String symbol, double price, int maxSize) {
        map.computeIfAbsent(symbol, k -> new ConcurrentLinkedDeque<>());
        Deque<Double> q = map.get(symbol);
        q.addLast(price);
        while (q.size() > maxSize) {
            q.removeFirst();
        }
    }
}

