package io.github.sabuwalamustafa;

import com.sabu.at.DateTimeUtils;
import io.github.sabuwalamustafa.converters.OrderConverter;
import io.github.sabuwalamustafa.interfaces.IBrokerUtils;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.LTPQuote;
import com.zerodhatech.models.Margin;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import io.github.sabuwalamustafa.interfaces.IFileUtils;
import io.github.sabuwalamustafa.interfaces.ILogStuff;
import io.github.sabuwalamustafa.models.OrderInternal;
import io.github.sabuwalamustafa.models.ResponseWrapper;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class ZerodhaUtils implements IBrokerUtils {
    private static final String BROKER_ID = "ZERODHA_API";
    private static ZerodhaUtils INSTANCE;
    private KiteConnect kiteSdk;
    private ZerodhaTokensProvider zerodhaTokensProvider;
    private ILogStuff logStuff;
    private IFileUtils gfsFileUtils;
    private FilePathsProvider filePathsProvider;

    // zerodhaConfig should contain following keys:
    // zerodha_api
    // zerodha_user_id
    private ZerodhaUtils(ILogStuff logStuff,
            Map<String, String> zerodhaConfig) {
        // todo: better way to pass zerodha config than a map<>
        this.logStuff = logStuff;
        this.filePathsProvider = new FilePathsProvider();
        this.gfsFileUtils = GFSFileUtilsFactory.getGFSFileUtils(
                filePathsProvider.getGoogleKeyFilePath());
        init(zerodhaConfig.get("zerodha_api"),
             zerodhaConfig.get("zerodha_user_id"));
    }

    public static ZerodhaUtils getInstance(ILogStuff logStuff,
            Map<String, String> zerodhaConfig) {
        if (INSTANCE == null) {
            INSTANCE = new ZerodhaUtils(logStuff, zerodhaConfig);
        }
        return INSTANCE;
    }

    private void init(String zerodhaApi, String zerodhaUserId) {
        zerodhaTokensProvider = new ZerodhaTokensProvider(gfsFileUtils,
                                                          filePathsProvider);

        setUpKiteSdk(zerodhaApi, zerodhaUserId);

        kiteSdk.setSessionExpiryHook(() -> {
            logStuff.datedLogIt("session expired");
            setUpKiteSdk(zerodhaApi, zerodhaUserId);
        });
    }

    private void setUpKiteSdk(String zerodhaApi, String zerodhaUserId) {
        Map<String, String> map;
        try {
            map = zerodhaTokensProvider.provide();
        } catch (IOException e) {
            // todo log
            return;
        }
        String accessToken = map.get("access_token");
        String publicToken = map.get("public_token");
        kiteSdk = new KiteConnect(zerodhaApi);
        kiteSdk.setUserId(zerodhaUserId);
        kiteSdk.setAccessToken(accessToken);
        kiteSdk.setPublicToken(publicToken);
    }

    // Tested, status: WORKING
    @Override public ResponseWrapper<Double> getFundsAvailable() {
        ResponseWrapper.ResponseWrapperBuilder<Double> responseWrapper
                = ResponseWrapper.builder();
        try {
            Margin margins = kiteSdk.getMargins("equity");
            responseWrapper.tResponse(
                    Double.parseDouble(margins.available.liveBalance));
            responseWrapper.isSuccessful(true);
        } catch (KiteException e) {
            // todo log
        } catch (IOException e) {
            // todo log
        }
        return responseWrapper.build();
    }

    // Tested, status: WORKING (with an assumption)
    // Returns the latest order state details.
    @Override
    public ResponseWrapper<OrderInternal> getOrderDetails(String orderId) {
        ResponseWrapper.ResponseWrapperBuilder<OrderInternal> responseWrapper
                = ResponseWrapper.builder();
        try {
            List<Order> orders = kiteSdk.getOrderHistory(orderId);
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
            if (orders != null && !orders.isEmpty()) {
                // todo confirm correct logic for finding latest order.
                Order latestOrder = orders.get(orders.size() - 1);
                OrderInternal oi = OrderConverter.toOrder(latestOrder);
                responseWrapper.tResponse(oi);
                responseWrapper.isSuccessful(true);
            }
        } catch (KiteException e) {
            // todo log
        } catch (IOException e) {
            // todo log
        }
        return responseWrapper.build();
    }

    // Tested, status: WORKING
    @Override public ResponseWrapper<Double> getLtp(String symbol) {
        ResponseWrapper.ResponseWrapperBuilder<Double> responseWrapper
                = ResponseWrapper.builder();
        try {
            String nseSymbol = utils_utils.getNseSymbol(symbol);
            Map<String, LTPQuote> ltpQuote = kiteSdk.getLTP(
                    new String[]{nseSymbol});
            responseWrapper.tResponse(ltpQuote.get(nseSymbol).lastPrice);
            responseWrapper.isSuccessful(true);
        } catch (KiteException e) {
            // todo log
        } catch (IOException e) {
            // todo log
        }
        return responseWrapper.build();
    }

    // Tested, status: WORKING
    @Override
    public ResponseWrapper<String> placeBuyOrder(String symbol, double quantity,
            double price) {
        ResponseWrapper.ResponseWrapperBuilder<String> responseWrapper
                = ResponseWrapper.builder();
        OrderParams orderParams = new OrderParams();
        orderParams.quantity = (int) quantity;
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = symbol;
        orderParams.product = Constants.PRODUCT_CNC;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = price;
        // Can set tag as well

        try {
            Order order = kiteSdk.placeOrder(orderParams,
                                             Constants.VARIETY_REGULAR);
            responseWrapper.strResponse(order.orderId);
            responseWrapper.isSuccessful(true);
        } catch (KiteException e) {
            // todo log
        } catch (IOException e) {
            // todo log
        }
        return responseWrapper.build();
    }

    // Tested, status: WORKING
    @Override public ResponseWrapper<String> placeSellOrder(String symbol,
            double quantity, double price) {
        ResponseWrapper.ResponseWrapperBuilder<String> responseWrapper
                = ResponseWrapper.builder();
        OrderParams orderParams = new OrderParams();
        orderParams.quantity = (int) quantity;
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = symbol;
        orderParams.product = Constants.PRODUCT_CNC;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_SELL;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = price;
        // Can set tag as well

        try {
            Order order = kiteSdk.placeOrder(orderParams,
                                             Constants.VARIETY_REGULAR);
            responseWrapper.strResponse(order.orderId);
            responseWrapper.isSuccessful(true);
        } catch (KiteException e) {
            logStuff.datedLogIt(e.getMessage());
        } catch (IOException e) {
            logStuff.datedLogIt(e.getMessage());
        }
        return responseWrapper.build();
    }

    // Tested, status: WORKING
    @Override
    public ResponseWrapper<String> getLtpSymbolList(List<String> symbols) {
        ResponseWrapper.ResponseWrapperBuilder<String> responseWrapper
                = ResponseWrapper.builder();
        try {
            List<String> nseSymbols = symbols.stream().map(
                    symbol -> utils_utils.getNseSymbol(symbol)).collect(
                    Collectors.toList());
            Map<String, LTPQuote> ltpQuote = kiteSdk.getLTP(
                    nseSymbols.toArray(new String[0]));
            // todo: Should be String, Double map
            Map<String, String> res = ltpQuote.entrySet().stream().collect(
                    Collectors.toMap(entry -> utils_utils.reverseNseSymbol(
                                             entry.getKey()),
                                     entry -> String.valueOf(
                                             entry.getValue().lastPrice)));
            responseWrapper.mapResponse(res);
            responseWrapper.isSuccessful(true);
        } catch (KiteException e) {
            // todo log
        } catch (IOException e) {
            // todo log
        }
        return responseWrapper.build();
    }

    public boolean isHoliday(LocalDate date) {
        Set<LocalDate> holidays = new HashSet<>();
        // todo: Populate holidays set with your specific holiday dates
        // e.g., holidays.add(LocalDate.of(year, month, day));

        return holidays.contains(date);
    }

    @Override public boolean isMarketOpen() {
        LocalDate today = DateTimeUtils.getTodaysDate();
        boolean isWeekend = today.getDayOfWeek() == DayOfWeek.SATURDAY
                            || today.getDayOfWeek() == DayOfWeek.SUNDAY;

        if (isHoliday(today) || isWeekend) {
            return false;
        } else {
            LocalTime marketStartTime = LocalTime.of(9, 15);
            LocalTime marketEndTime = LocalTime.of(15, 29);
            LocalDateTime now = DateTimeUtils.getCurrentIstTimestamp();
            LocalDateTime marketStart = LocalDateTime.of(today,
                                                         marketStartTime);
            LocalDateTime marketEnd = LocalDateTime.of(today, marketEndTime);

            return now.isAfter(marketStart) && now.isBefore(marketEnd);
        }
    }

    @Override public ResponseWrapper<List<OrderInternal>> getAllOrders(
            List<String> symbols, LocalDateTime startTime) {
        // todo
        throw new RuntimeException(
                "Zerodha.getAllOrders() is unimplemented...");
    }

    @Override
    public ResponseWrapper<List<OrderInternal>> getAllOrders(String symbol,
            LocalDateTime startTime) {
        // todo
        throw new RuntimeException(
                "Zerodha.getAllOrders() is unimplemented...");
    }

    @Override public String getBrokerId() {
        return BROKER_ID;
    }

    @Override public void noteTheBuyOrderPlaced(String orderId) {
        // todo
        throw new RuntimeException(
                "Zerodha.noteTheBuyOrderPlaced() is unimplemented...");
    }

    @Override public void noteTheSellOrderPlaced(String orderId, String refId) {
        // todo
        throw new RuntimeException(
                "Zerodha.noteTheSellOrderPlaced() is unimplemented...");
    }
}
