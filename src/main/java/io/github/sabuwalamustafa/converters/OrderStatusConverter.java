package io.github.sabuwalamustafa.converters;


import io.github.sabuwalamustafa.models.OrderStatus;

public class OrderStatusConverter {
    public static String toBrokerOrderStatus(OrderStatus orderStatus) {
        switch (orderStatus) {
            case OPEN:
                return "OPEN";
            case CANCELLED:
                return "CANCELLED";
            case COMPLETED:
                return "COMPLETE";
            case REJECTED:
                return "REJECTED";
            case MISSING_ORDER_STATUS:
            default:
                throw new RuntimeException(
                        "Missing order status while conversion!");
        }
    }

    // todo: check if string values are correct.
    public static OrderStatus toGenericOrderStatus(String brokerOrderStatus) {
        switch (brokerOrderStatus) {
            case "OPEN":
            case "PUT ORDER REQ RECEIVED":
            case "VALIDATION PENDING":
            case "OPEN PENDING":
            case "MODIFY VALIDATION PENDING":
            case "MODIFY PENDING":
            case "TRIGGER PENDING":
            case "CANCEL PENDING":
            case "AMO REQ RECEIVED":
                return OrderStatus.OPEN;// todo: Partial Order status handling
            case "COMPLETE":
                return OrderStatus.COMPLETED;
            case "CANCELLED":
                return OrderStatus.CANCELLED;
            case "REJECTED":
                return OrderStatus.REJECTED;
            default:
                return OrderStatus.MISSING_ORDER_STATUS;
        }
    }

    /*
     * Zerodha Order statuses:
     * https://kite.trade/docs/connect/v3/orders/#order-statuses
     *
     * OPEN
     * COMPLETE
     * CANCELLED
     * REJECTED
     * PUT ORDER REQ RECEIVED
     * VALIDATION PENDING
     * OPEN PENDING
     * MODIFY VALIDATION PENDING
     * MODIFY PENDING
     * TRIGGER PENDING
     * CANCEL PENDING
     * AMO REQ RECEIVED
     * */
}