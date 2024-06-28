package io.github.sabuwalamustafa;

import com.google.auth.Credentials;
import com.sabu.at.DateTimeUtils;
import com.zerodhatech.models.*;
import io.github.sabuwalamustafa.converters.OrderConverter;
import io.github.sabuwalamustafa.interfaces.IBrokerUtils;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import io.github.sabuwalamustafa.interfaces.IFileUtils;
import io.github.sabuwalamustafa.interfaces.ILogStuff;
import io.github.sabuwalamustafa.models.DatabaseConfig;
import io.github.sabuwalamustafa.models.OrderCore;
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
    private OrderStoreWrapper orderStoreWrapper;
    private ZerodhaUtilsHelper zerodhaUtilsHelper;

    // zerodhaConfig should contain following keys:
    // zerodha_api
    // zerodha_user_id
    private ZerodhaUtils(ILogStuff logStuff, Map<String, String> zerodhaConfig,
            Credentials credentials, DatabaseConfig databaseConfig) {
        // todo: better way to pass zerodha config than a map<>
        this.logStuff = logStuff;
        this.filePathsProvider = new FilePathsProvider();
        this.gfsFileUtils = GFSFileUtilsFactory.getGFSFileUtils(credentials);
        this.orderStoreWrapper = new OrderStoreWrapper(databaseConfig);

        init(zerodhaConfig.get("zerodha_api"),
             zerodhaConfig.get("zerodha_user_id"));
    }

    public static ZerodhaUtils getInstance(ILogStuff logStuff,
            Map<String, String> zerodhaConfig, Credentials credentials,
            DatabaseConfig databaseConfig) {
        if (INSTANCE == null) {
            INSTANCE = new ZerodhaUtils(logStuff, zerodhaConfig, credentials,
                                        databaseConfig);
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

        List<Instrument> instruments;
        try {
            instruments = kiteSdk.getInstruments("NSE");
        } catch (KiteException | IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> instrumentsMap;
        if (instruments != null) {
            instrumentsMap = new HashMap<>();
            instruments.forEach(instrument -> instrumentsMap.put(
                    instrument.getTradingsymbol(),
                    String.valueOf(instrument.getInstrument_token())));
        } else {
            instrumentsMap = null;
        }
        this.zerodhaUtilsHelper = new ZerodhaUtilsHelper(logStuff,
                                                         instrumentsMap);
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseWrapper.build();
    }

    // todo: testing pending
    // Returns the latest order state details.
    @Override public ResponseWrapper<OrderInternal> getOrderDetails(
            String orderId) {
        // todo: handling of GTT orders.
        OrderInternal oi = null;
        OrderInternal oiFromOrderStore = orderStoreWrapper.getOrder(orderId);
        if (oiFromOrderStore != null && utils_utils.isTerminalStatus(
                oiFromOrderStore.getStatus())) {
            oi = oiFromOrderStore;
        } else {
            // todo: Consider and evaluate querying zerodha first.
            try {
                List<Order> orders = kiteSdk.getOrderHistory(orderId);
                Order latestOrder = ZerodhaUtilsHelper.getRelevantOrder(orders);
                oi = (latestOrder == null) ? null :
                     OrderConverter.toOrder(latestOrder);
            } catch (KiteException | IOException e) {
                // todo log
            }
        }

        ResponseWrapper.ResponseWrapperBuilder<OrderInternal> responseWrapper
                = ResponseWrapper.builder();
        if (oi != null) {
            responseWrapper.tResponse(oi);
            responseWrapper.isSuccessful(true);
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
    @Override public ResponseWrapper<String> placeBuyOrder(
            OrderCore orderCore) {
        ResponseWrapper.ResponseWrapperBuilder<String> responseWrapper
                = ResponseWrapper.builder();
        OrderParams orderParams = new OrderParams();
        orderParams.quantity = (int) orderCore.getQuantity();
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = orderCore.getSymbol();
        orderParams.product = Constants.PRODUCT_CNC;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = orderCore.getPrice();
        // Can set tag as well

        try {
            Order order = kiteSdk.placeOrder(orderParams,
                                             Constants.VARIETY_REGULAR);
            responseWrapper.tResponse(order.orderId);
            responseWrapper.isSuccessful(true);

            OrderInternal orderInternal = OrderConverter.toOrder(order);
            noteTheBuyOrderPlaced(orderInternal);
        } catch (KiteException e) {
            // todo log
        } catch (IOException e) {
            // todo log
        }
        return responseWrapper.build();
    }

    // Tested, status: WORKING
    @Override public ResponseWrapper<String> placeSellOrder(
            OrderCore orderCore) {
        ResponseWrapper.ResponseWrapperBuilder<String> responseWrapper
                = ResponseWrapper.builder();
        OrderParams orderParams = new OrderParams();
        orderParams.quantity = (int) orderCore.getQuantity();
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = orderCore.getSymbol();
        orderParams.product = Constants.PRODUCT_CNC;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_SELL;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = orderCore.getPrice();
        // Can set tag as well

        try {
            Order order = kiteSdk.placeOrder(orderParams,
                                             Constants.VARIETY_REGULAR);
            responseWrapper.tResponse(order.orderId);
            responseWrapper.isSuccessful(true);

            OrderInternal orderInternal = OrderConverter.toOrder(order);
            noteTheSellOrderPlaced(orderInternal);
        } catch (KiteException | IOException e) {
            logStuff.datedLogIt(e.getMessage());
        }
        return responseWrapper.build();
    }

    // Tested, status: WORKING
    @Override public ResponseWrapper<Map<String, String>> getLtpSymbolList(
            List<String> symbols) {
        ResponseWrapper.ResponseWrapperBuilder<Map<String, String>>
                responseWrapper = ResponseWrapper.builder();
        try {
            List<String> nseSymbols = symbols.stream().map(
                    utils_utils::getNseSymbol).collect(
                    Collectors.toList());
            Map<String, LTPQuote> ltpQuote = kiteSdk.getLTP(
                    nseSymbols.toArray(new String[0]));
            // todo: Should be String, Double map
            Map<String, String> res = ltpQuote.entrySet().stream().collect(
                    Collectors.toMap(entry -> utils_utils.reverseNseSymbol(
                            entry.getKey()), entry -> String.valueOf(
                            entry.getValue().lastPrice)));
            responseWrapper.tResponse(res);
            responseWrapper.isSuccessful(true);
        } catch (KiteException e) {
            // todo log
        } catch (IOException e) {
            // todo log
        }
        return responseWrapper.build();
    }

    @Override public boolean isMarketOpen() {
        LocalDate today = DateTimeUtils.getTodaysDate();
        boolean isWeekend = today.getDayOfWeek() == DayOfWeek.SATURDAY
                            || today.getDayOfWeek() == DayOfWeek.SUNDAY;

        if (ZerodhaUtilsHelper.isHoliday(today) || isWeekend) {
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

    // todo: testing pending
    // Returns only *today's* orders.
    @Override public ResponseWrapper<List<OrderInternal>> getAllOrders(
            List<String> symbols, LocalDateTime startTime) {
        List<OrderInternal> ordersFromZerodha
                = zerodhaUtilsHelper.getOrdersFromZerodha(kiteSdk);

        List<OrderInternal> ordersFromOrderStore = null;
        try {
            ordersFromOrderStore = zerodhaUtilsHelper.getAllOrdersFromOrderStore(
                    orderStoreWrapper);
        } catch (Exception e) {
            // todo: log
        }

        // Merging logic START.
        Set<String> orderIdSetFromZerodha = ordersFromZerodha != null ?
                                            ordersFromZerodha.stream()
                                                             .map(OrderInternal::getId)
                                                             .collect(
                                                                     Collectors.toSet()) :
                                            new HashSet<>();
        List<OrderInternal> mergedOrders = new ArrayList<>();

        if (ordersFromZerodha != null) {
            mergedOrders.addAll(ordersFromZerodha);
        }

        if (ordersFromOrderStore != null) {
            for (OrderInternal oi : ordersFromOrderStore) {
                if (orderIdSetFromZerodha.contains(oi.getId())) {
                    continue;
                }
                mergedOrders.add(oi);
            }
        }
        // Merging logic END.

        // Filtering based on symbol and startTime START.
        mergedOrders = mergedOrders.stream().filter(
                oi -> symbols.contains(oi.getSymbol())).filter(
                oi -> startTime == null || startTime.equals(oi.getPlacedAt())
                      || startTime.isBefore(
                        oi.getPlacedAt())).collect(
                Collectors.toList());
        // Filtering based on symbol and startTime END.

        ResponseWrapper.ResponseWrapperBuilder<List<OrderInternal>>
                responseWrapper = ResponseWrapper.builder();
        responseWrapper.tResponse(mergedOrders);
        responseWrapper.isSuccessful(true);
        return responseWrapper.build();
    }

    @Override
    public ResponseWrapper<List<OrderInternal>> getAllOrders(String symbol,
            LocalDateTime startTime) {
        return getAllOrders(List.of(symbol), startTime);
    }

    @Override public ResponseWrapper<List<HistoricalData>> getHistoricalData(
            String symbol, String startTimestampStr,
            String endTimestampInclusiveStr) {
        Date startTimestamp = DateTimeUtils.parseToUtilDate(startTimestampStr);
        Date endTimestampInclusive = DateTimeUtils.parseToUtilDate(
                endTimestampInclusiveStr);
        ResponseWrapper.ResponseWrapperBuilder<List<HistoricalData>> rwBuilder
                = ResponseWrapper.builder();
        String symbolToken = zerodhaUtilsHelper.getInstrumentToken(symbol);
        if (symbolToken == null) {
            return rwBuilder.build();
        }
        try {
            HistoricalData historicalData = kiteSdk.getHistoricalData(
                    startTimestamp, endTimestampInclusive, symbolToken,
                    "minute", false, true);
            rwBuilder.tResponse(historicalData.dataArrayList);
            rwBuilder.isSuccessful(true);
        } catch (KiteException e) {
            logStuff.datedLogIt(e.getMessage());
        } catch (IOException e) {
            logStuff.datedLogIt(e.getMessage());
        }

        return rwBuilder.build();
    }

    @Override public String getBrokerId() {
        return BROKER_ID;
    }

    @Override public void noteTheBuyOrderPlaced(OrderInternal orderInternal) {
        // todo: Think if something else is needed here.
        orderStoreWrapper.onSuccessfulOrderPlacement(orderInternal);
    }

    @Override public void noteTheSellOrderPlaced(OrderInternal orderInternal) {
        // todo: Think if something else is needed here.
        orderStoreWrapper.onSuccessfulOrderPlacement(orderInternal);
    }
}
