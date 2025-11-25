package org.pm.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "positions", indexes = {
    @Index(name = "idx_position_symbol", columnList = "symbol", unique = true)
})
@Data
@NoArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", unique = true, nullable = false, length = 20)
    private String symbol;

    @Column(name = "quantity")
    private double quantity;
    
    @Column(name = "average_price")
    private double averagePrice;
    
    @Column(name = "current_price")
    private double currentPrice;
    
    @Column(name = "realized_pnl")
    private double realizedPnL;
    
    @Column(name = "unrealized_pnl")
    private double unrealizedPnL;
    
    @Column(name = "last_updated")
    private long lastUpdated;

    public Position(String symbol) {
        this.symbol = symbol;
        this.quantity = 0.0;
        this.averagePrice = 0.0;
        this.currentPrice = 0.0;
        this.realizedPnL = 0.0;
        this.unrealizedPnL = 0.0;
        this.lastUpdated = System.currentTimeMillis();
    }
}
