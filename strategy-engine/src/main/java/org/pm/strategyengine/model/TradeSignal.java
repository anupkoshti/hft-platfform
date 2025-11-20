package org.pm.strategyengine.model;

import java.io.Serializable;

public class TradeSignal implements Serializable {
    private String symbol;
    private String signal; // BUY / SELL / HOLD
    private double price;
    private long timestamp;

    public TradeSignal() {}

    public TradeSignal(String symbol, String signal, double price, long timestamp) {
        this.symbol = symbol;
        this.signal = signal;
        this.price = price;
        this.timestamp = timestamp;
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getSignal() { return signal; }
    public void setSignal(String signal) { this.signal = signal; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "TradeSignal{" +
                "symbol='" + symbol + '\'' +
                ", signal='" + signal + '\'' +
                ", price=" + price +
                ", timestamp=" + timestamp +
                '}';
    }
}
