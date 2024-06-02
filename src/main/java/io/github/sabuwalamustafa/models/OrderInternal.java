package io.github.sabuwalamustafa.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderInternal {
    private String symbol;
    private String id;
    private Double price;
    private Double filledAveragePrice;
    private Double quantity;
    private Double filledQuantity;
    private OrderStatus status;
    private LocalDateTime placedAt;
    private LocalDateTime updatedAt;
    private OrderTransactionType transactionType; // {BUY, SELL}
    private String limitOrMarket;
    private LocalDateTime filledAt; // Not populated for wazirx

    public OrderInternal() {
        this.filledQuantity = 0.0;
        this.status = OrderStatus.OPEN;
    }

    public static boolean isNonTerminalStatus(OrderStatus orderStatus) {
        // todo: Partial order status
        return orderStatus.equals(OrderStatus.OPEN);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    // Additional getters and setters...

    // Method to convert Order details to String
    @Override
    public String toString() {
        return "Order{" +
               "symbol='" + symbol + '\'' +
               ", orderId='" + id + '\'' +
               ", price=" + price +
               ", filledAveragePrice=" + filledAveragePrice +
               ", quantity=" + quantity +
               ", filledQuantity=" + filledQuantity +
               ", status=" + status +
               ", placedAt=" + placedAt +
               ", orderType='" + transactionType.toString() + '\'' +
               ", filledAt=" + filledAt +
               '}';
    }
}