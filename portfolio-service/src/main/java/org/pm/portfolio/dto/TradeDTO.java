package org.pm.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeDTO {
    private Long id;
    private String tradeId;
    private String brokerOrderId;
    private String symbol;
    private String side;
    private double price;
    private double quantity;
    private String status;
    private long timestamp;
}
