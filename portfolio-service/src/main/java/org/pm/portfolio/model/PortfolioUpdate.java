package org.pm.portfolio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioUpdate {
    private double totalValue;
    private double totalRealizedPnL;
    private double totalUnrealizedPnL;
    private double totalPnL;
    private List<PositionSummary> positions;
    private long timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionSummary {
        private String symbol;
        private double quantity;
        private double averagePrice;
        private double currentPrice;
        private double unrealizedPnL;
        private double unrealizedPnLPercent;
    }
}
