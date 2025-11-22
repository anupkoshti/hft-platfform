package org.pm.orderexecution.model;

public class OrderStatusEvent {
    private String tradeId;
    private String brokerOrderId;
    private String symbol;
    private String side;
    private double price;
    private String status; // ACCEPTED / FILLED / REJECTED / ERROR
    private long timestamp;
    private String message;

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getBrokerOrderId() {
        return brokerOrderId;
    }

    public void setBrokerOrderId(String brokerOrderId) {
        this.brokerOrderId = brokerOrderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "OrderStatusEvent{" +
                "tradeId='" + tradeId + '\'' +
                ", brokerOrderId='" + brokerOrderId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", side='" + side + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                '}';
    }
}
