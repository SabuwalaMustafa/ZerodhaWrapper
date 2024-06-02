package io.github.sabuwalamustafa.interfaces;

import io.github.sabuwalamustafa.models.OrderInternal;
import io.github.sabuwalamustafa.models.ResponseWrapper;

import java.time.LocalDateTime;
import java.util.List;

public interface IBrokerUtils {
    ResponseWrapper<Double> getFundsAvailable();

    ResponseWrapper<OrderInternal> getOrderDetails(String orderId);

    ResponseWrapper<Double> getLtp(String symbol);

    ResponseWrapper<String> placeBuyOrder(String symbol, double quantity,
            double price);

    ResponseWrapper<String> placeSellOrder(String symbol, double quantity,
            double price);

    ResponseWrapper<String> getLtpSymbolList(List<String> symbols);

    boolean isMarketOpen();

    ResponseWrapper<List<OrderInternal>> getAllOrders(List<String> symbols,
            LocalDateTime startTime);

    ResponseWrapper<List<OrderInternal>> getAllOrders(String symbol,
            LocalDateTime startTime);

    String getBrokerId();

    void noteTheBuyOrderPlaced(String orderId);
    void noteTheSellOrderPlaced(String orderId, String refId);
}