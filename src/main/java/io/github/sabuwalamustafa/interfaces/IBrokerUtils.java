package io.github.sabuwalamustafa.interfaces;

import com.zerodhatech.models.HistoricalData;
import io.github.sabuwalamustafa.models.OrderCore;
import io.github.sabuwalamustafa.models.OrderInternal;
import io.github.sabuwalamustafa.models.ResponseWrapper;
import io.github.sabuwalamustafa.models.brokermodels.HoldingInternal;
import io.github.sabuwalamustafa.models.brokermodels.PositionInternal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IBrokerUtils {
    ResponseWrapper<Double> getFundsAvailable();

    ResponseWrapper<OrderInternal> getOrderDetails(String orderId);

    ResponseWrapper<Double> getLtp(String symbol);

    ResponseWrapper<String> placeBuyOrder(OrderCore orderCore);

    ResponseWrapper<String> placeSellOrder(OrderCore orderCore);

    ResponseWrapper<Map<String, String>> getLtpSymbolList(List<String> symbols);

    boolean isMarketOpen();

    ResponseWrapper<List<OrderInternal>> getAllOrders(List<String> symbols,
            LocalDateTime startTime);

    ResponseWrapper<List<OrderInternal>> getAllOrders(String symbol,
            LocalDateTime startTime);

    ResponseWrapper<List<HistoricalData>> getHistoricalData(
            String symbol,
            String startTimestampStr, String endTimestampInclusiveStr);

    ResponseWrapper<List<HoldingInternal>> getHoldings(List<String> symbol);

    ResponseWrapper<List<PositionInternal>> getPositions(List<String> symbol);

    String getBrokerId();

    void noteTheBuyOrderPlaced(String orderId);

    void noteTheBuyOrderPlaced(OrderInternal orderInternal);

    void noteTheSellOrderPlaced(String orderId);

    void noteTheSellOrderPlaced(OrderInternal orderInternal);
}