package org.pm.orderexecution.broker;

import org.pm.common.model.ValidatedTrade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Broker Client - Handles order execution via broker API
 * Currently uses MockBrokerService for testing
 * In production, replace with real broker integration
 */
@Component
public class BrokerClient {

    private final MockBrokerService mockBroker;
    
    @Value("${broker.use-mock:true}")
    private boolean useMock;

    public BrokerClient(MockBrokerService mockBroker) {
        this.mockBroker = mockBroker;
    }

    public Mono<BrokerOrderResponse> placeOrder(ValidatedTrade validatedTrade, String tradeId) {
        // For now, always use mock broker
        // TODO: In production, check useMock flag and call real broker API
        return mockBroker.placeOrder(validatedTrade, tradeId)
                .map(mockResponse -> {
                    BrokerOrderResponse response = new BrokerOrderResponse();
                    response.orderId = mockResponse.orderId;
                    response.status = mockResponse.status;
                    response.timestamp = mockResponse.timestamp;
                    return response;
                })
                .doOnError(err -> System.err.println("Broker error for tradeId=" + tradeId + " -> " + err.getMessage()));
    }

    /**
     * Response from broker
     */
    public static class BrokerOrderResponse {
        public String orderId;
        public String status;
        public long timestamp;
    }
}
