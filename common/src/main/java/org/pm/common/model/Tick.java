package org.pm.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tick implements Serializable {
    private String symbol;
    private double price;
    private double quantity;
    private long timestamp;

    public Tick() {}

    public Tick(String symbol, double price, double quantity, long timestamp) {
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getSymbol() { 
        return symbol; 
    }
    
    public void setSymbol(String symbol) { 
        this.symbol = symbol; 
    }

    public double getPrice() { 
        return price; 
    }
    
    public void setPrice(double price) { 
        this.price = price; 
    }

    public double getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(double quantity) { 
        this.quantity = quantity; 
    }

    public long getTimestamp() { 
        return timestamp; 
    }
    
    public void setTimestamp(long timestamp) { 
        this.timestamp = timestamp; 
    }

    @Override
    public String toString() {
        return "Tick{" +
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", timestamp=" + timestamp +
                '}';
    }
}
