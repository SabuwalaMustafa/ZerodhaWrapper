package io.github.sabuwalamustafa;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Order;
import io.github.sabuwalamustafa.converters.OrderConverter;
import io.github.sabuwalamustafa.interfaces.ILogStuff;
import io.github.sabuwalamustafa.models.OrderInternal;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ZerodhaUtilsHelper {
    private ILogStuff logStuff;
    private Map<String, String> instrumentsMap;

    ZerodhaUtilsHelper(ILogStuff logStuff,
            Map<String, String> instrumentsMap) {
        this.logStuff = logStuff;
        this.instrumentsMap = instrumentsMap;
    }

    static boolean isHoliday(LocalDate date) {
        Set<LocalDate> holidays = new HashSet<>();
        // todo: Populate holidays set with your specific holiday dates
        // e.g., holidays.add(LocalDate.of(year, month, day));

        return holidays.contains(date);
    }

    static Order getRelevantOrder(List<Order> orders) {
        if (orders != null && !orders.isEmpty()) {
            // todo confirm correct logic for finding latest order.

//            Order latestOrder = orders.stream().sorted(new
//            Comparator<Order>() {
//                @Override public int compare(Order o1, Order o2) {
//                    if (o1.exchangeUpdateTimestamp == null
//                        && o2.exchangeUpdateTimestamp == null) {
//                        // todo: Ideally, should be ordered by o.status
//                        //  logical ordering
//                        return o1.status.compareTo(o2.status);
//                    }
//                    if (o1.exchangeUpdateTimestamp == null) {
//                        return 1;
//                    }
//                    if (o2.exchangeUpdateTimestamp == null) {
//                        return -1;
//                    }
//                    return -o1.exchangeUpdateTimestamp.compareTo(
//                            o2.exchangeUpdateTimestamp);
//                }
//            }).findFirst().get();
            return orders.get(orders.size() - 1);
        }
        return null;
    }

    List<OrderInternal> getOrdersFromZerodha(
            KiteConnect kiteSdk) {
        List<OrderInternal> oil = null;
        try {
            oil = kiteSdk.getOrders().stream().map(
                    OrderConverter::toOrder).collect(
                    Collectors.toList());
        } catch (KiteException e) {
            logStuff.datedLogIt(e.getMessage());
        } catch (IOException e) {
            logStuff.datedLogIt(e.getMessage());
        }
        return oil;
    }

    List<OrderInternal> getOrdersFromOrderStore(
            OrderStoreWrapper orderStoreWrapper) {
        // Assumption here is that OrderStore would have ALL the orderIds.
        return orderStoreWrapper.getAllOrders();
    }

    String getInstrumentToken(String symbol) {
        // note: Intentionally skipped instrumentsMap null check.
        return instrumentsMap.getOrDefault(symbol, null);
    }
}
