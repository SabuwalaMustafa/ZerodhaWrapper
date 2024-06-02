package io.github.sabuwalamustafa.models;

public enum OrderTransactionType {
    BUY, SELL;
    public String asString() {
        return this.name().toLowerCase();
    }
}
