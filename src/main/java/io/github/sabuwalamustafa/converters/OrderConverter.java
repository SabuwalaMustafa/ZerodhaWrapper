package io.github.sabuwalamustafa.converters;

import com.sabu.at.DateTimeUtils;
import com.zerodhatech.models.Order;
import io.github.sabuwalamustafa.models.OrderInternal;
import io.github.sabuwalamustafa.models.OrderTransactionType;

// todo: write tests.
public class OrderConverter {
    public static Order toBrokerOrder(OrderInternal o) {
        // todo
        Order zerodhaOrder = new Order();
        zerodhaOrder.tradingSymbol = o.getSymbol();
        zerodhaOrder.orderId = o.getId();
        zerodhaOrder.quantity = String.valueOf(o.getQuantity());
        zerodhaOrder.price = String.valueOf(o.getPrice());
        // todo test order status value
        zerodhaOrder.status = OrderStatusConverter.toBrokerOrderStatus(o.getStatus());
        zerodhaOrder.averagePrice = String.valueOf(o.getFilledAveragePrice());
        zerodhaOrder.orderType = o.getLimitOrMarket();
        zerodhaOrder.transactionType = transactionTypeConverter(o.getTransactionType());
        zerodhaOrder.filledQuantity = String.valueOf(o.getFilledQuantity());
        zerodhaOrder.orderTimestamp = DateTimeUtils.convert(o.getPlacedAt());
        zerodhaOrder.exchangeUpdateTimestamp = DateTimeUtils.convert(o.getUpdatedAt());

        // todo support for filled timestamp

        return zerodhaOrder;
    }

    public static OrderInternal toOrder(Order zerodhaOrder) {
        // todo
        OrderInternal o = new OrderInternal();
        o.setSymbol(zerodhaOrder.tradingSymbol);
        o.setId(zerodhaOrder.orderId);
        o.setQuantity(Double.parseDouble(zerodhaOrder.quantity));
        o.setPrice(Double.parseDouble(zerodhaOrder.price));
        o.setStatus(OrderStatusConverter.toGenericOrderStatus(
                zerodhaOrder.status));
        o.setFilledAveragePrice(Double.parseDouble(zerodhaOrder.averagePrice));
        o.setLimitOrMarket(zerodhaOrder.orderType);
        // todo transactiontype
        o.setTransactionType(
                transactionTypeConverter(zerodhaOrder.transactionType));
        o.setFilledQuantity(Double.parseDouble(zerodhaOrder.filledQuantity));
        o.setPlacedAt(DateTimeUtils.convert(zerodhaOrder.orderTimestamp));
        o.setUpdatedAt(
                DateTimeUtils.convert(zerodhaOrder.exchangeUpdateTimestamp));

        // todo support for filled timestamp

        return o;
    }

    // todo: not the right place
    private static OrderTransactionType transactionTypeConverter(String side) {
        if (side.equalsIgnoreCase("BUY")) {
            return OrderTransactionType.BUY;
        }
        return OrderTransactionType.SELL;
    }

    // todo: not the right place
    private static String transactionTypeConverter(OrderTransactionType side) {
        if (side.equals(OrderTransactionType.BUY)) {
            return "BUY";
        }
        return "SELL";
    }
}
