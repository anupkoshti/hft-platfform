package org.pm.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionDTO {
    private Long id;
    private String symbol;
    private double quantity;
    private double averagePrice;
    private double currentPrice;
    private double realizedPnL;
    private double unrealizedPnL;
    private long lastUpdated;
}
