package org.pm.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidatedTrade {
    private TradeSignal signal;
    private boolean valid;
    private String reason;

    public ValidatedTrade() {}

    public ValidatedTrade(TradeSignal signal, boolean valid, String reason) {
        this.signal = signal;
        this.valid = valid;
        this.reason = reason;
    }

    public TradeSignal getSignal() {
        return signal;
    }

    public void setSignal(TradeSignal signal) {
        this.signal = signal;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ValidatedTrade{" +
                "signal=" + signal +
                ", valid=" + valid +
                ", reason='" + reason + '\'' +
                '}';
    }
}
