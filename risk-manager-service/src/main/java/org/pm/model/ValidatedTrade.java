package org.pm.model;

import org.pm.common.model.TradeSignal;

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

    public static ValidatedTradeBuilder builder() {
        return new ValidatedTradeBuilder();
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

    public static class ValidatedTradeBuilder {
        private TradeSignal signal;
        private boolean valid;
        private String reason;

        public ValidatedTradeBuilder signal(TradeSignal signal) {
            this.signal = signal;
            return this;
        }

        public ValidatedTradeBuilder valid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public ValidatedTradeBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public ValidatedTrade build() {
            return new ValidatedTrade(signal, valid, reason);
        }
    }
}

