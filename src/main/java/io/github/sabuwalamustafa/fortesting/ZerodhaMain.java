package io.github.sabuwalamustafa.fortesting;


import io.github.sabuwalamustafa.ZerodhaUtils;

import java.util.List;

public class ZerodhaMain {
    public static void main(String[] args) {
        ZerodhaUtils zerodhaUtils = ZerodhaUtils.getInstance(null, null, null,
                                                             null);

//        String symbol = "RELIANCE";
//        System.out.println(zerodhaUtils.getLtp(symbol).getTResponse());

//        String[] symbols = new String[]{"RELIANCE", "HINDUNILVR", "PAGEIND"};
//        System.out.println(zerodhaUtils.getLtpSymbolList(List.of(symbols)));


//        System.out.println(zerodhaUtils.placeBuyOrder("ITBEES", 1, 34.99));

//        System.out.println(zerodhaUtils.placeSellOrder("ITBEES", 1, 34.99));

//        ResponseWrapper<OrderInternal> oi = zerodhaUtils.getOrderDetails(
//                "");
//        System.out.println(oi.toString());

        System.out.println(zerodhaUtils.getFundsAvailable());
    }
}
