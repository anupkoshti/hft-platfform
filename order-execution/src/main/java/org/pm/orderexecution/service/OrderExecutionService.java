package org.pm.orderexecution.service;

import org.pm.common.model.TradeSignal;
import org.pm.common.model.ValidatedTrade;
import org.pm.orderexecution.broker.BrokerClient;
import org.pm.orderexecution.broker.MockBrokerService;
import org.pm.orderexecution.model.OrderStatusEvent;
import org.pm.orderexecution.producer.OrderStatusProducer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderExecutionService {

    private final MockBrokerService mockBroker;
    private final OrderStatusProducer statusProducer;

    public OrderExecutionService(MockBrokerService mockBroker, OrderStatusProducer statusProducer) {
        this.mockBroker = mockBroker;
        this.statusProducer = statusProducer;
    }

    public void executeOrder(ValidatedTrade validatedTrade) {
        TradeSignal signal = validatedTrade.getSignal();
        String tradeId = "TRADE-" + UUID.randomUUID().toString().substring(0, 8);
        
        System.out.println("🔄 Executing order: " + signal.getSignal() + " " + signal.getSymbol() + " @ $" + signal.getPrice());

        Mono<MockBrokerService.BrokerOrderResponse> resp = mockBroker.placeOrder(validatedTrade, tradeId);

        resp.subscribe(
                r -> {
                    System.out.println("✅ Broker filled order: " + tradeId + " | brokerOrderId=" + r.orderId);
                    OrderStatusEvent s = new OrderStatusEvent();
                    s.setTradeId(tradeId);
                    s.setBrokerOrderId(r.orderId);
                    s.setSymbol(signal.getSymbol());
                    s.setSide(signal.getSignal());
                    s.setPrice(r.filledPrice);
                    s.setStatus("FILLED");
                    s.setTimestamp(r.timestamp);
                    s.setMessage("Order filled @ $" + String.format("%.2f", r.filledPrice));
                    statusProducer.publish(s);
                },
                err -> {
                    System.err.println("❌ Order failed: " + tradeId + " | " + err.getMessage());
                    OrderStatusEvent s = new OrderStatusEvent();
                    s.setTradeId(tradeId);
                    s.setBrokerOrderId(null);
                    s.setSymbol(signal.getSymbol());
                    s.setSide(signal.getSignal());
                    s.setPrice(signal.getPrice());
                    s.setStatus("REJECTED");
                    s.setTimestamp(Instant.now().toEpochMilli());
                    s.setMessage(err.getMessage());
                    statusProducer.publish(s);
                }
        );
    }
}
