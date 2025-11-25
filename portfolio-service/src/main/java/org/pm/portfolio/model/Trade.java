package org.pm.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trades", indexes = {
    @Index(name = "idx_trade_symbol", columnList = "symbol"),
    @Index(name = "idx_trade_timestamp", columnList = "timestamp"),
    @Index(name = "idx_trade_status", columnList = "status"),
    @Index(name = "idx_trade_symbol_timestamp", columnList = "symbol,timestamp")
})
@Data
@NoArgsConstructor
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_id")
    private String tradeId;
    
    @Column(name = "broker_order_id")
    private String brokerOrderId;
    
    @Column(name = "symbol", nullable = false)
    private String symbol;
    
    @Column(name = "side")
    private String side;
    
    @Column(name = "price")
    private double price;
    
    @Column(name = "quantity")
    private double quantity;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "timestamp", nullable = false)
    private long timestamp;

    public Trade(String tradeId, String brokerOrderId, String symbol, String side, 
                 double price, double quantity, String status, long timestamp) {
        this.tradeId = tradeId;
        this.brokerOrderId = brokerOrderId;
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.timestamp = timestamp;
    }
}
