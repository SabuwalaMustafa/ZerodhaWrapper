package io.github.sabuwalamustafa.fortesting;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.LTPQuote;
import com.zerodhatech.models.Margin;
import com.zerodhatech.models.User;

import java.io.IOException;
import java.util.Map;

/**
 * #testing
 */
class ZerodhaApiManualTester {
    public static final String ZERODHA_API = "";
    public static final String ZERODHA_SECRET = "";
    public static final String ZERODHA_USER_ID = "";

    public static void main(String[] args) throws IOException, KiteException {
        KiteConnect kiteSdk = new KiteConnect(ZERODHA_API);
        kiteSdk.setUserId(ZERODHA_USER_ID);

        String url = kiteSdk.getLoginURL();

        System.out.println(url);

        String accessToken = null;
        String publicToken = null;
        String requestToken = null;
        if (accessToken == null) {
            // Get accessToken as follows,
            User user = kiteSdk.generateSession(requestToken, ZERODHA_SECRET);
            accessToken = user.accessToken;
            publicToken = user.publicToken;
        }

        kiteSdk.setAccessToken(accessToken);
        kiteSdk.setPublicToken(publicToken);

        System.out.println("Access token: " + accessToken);
        System.out.println("Public token: " + publicToken);

        // Set session expiry callback.
        kiteSdk.setSessionExpiryHook(new SessionExpiryHook() {
            @Override
            public void sessionExpired() {
                System.out.println("session expired");
            }
        });

        Margin margins = kiteSdk.getMargins("equity");
        System.out.println(margins.available.cash);
        System.out.println(margins.utilised.debits);
        System.out.println(margins);
        String[] nseSymbols = new String[]{"NSE:RELIANCE"};
        Map<String, LTPQuote> ltpQuote = kiteSdk.getLTP(nseSymbols);
        System.out.println(ltpQuote.get(nseSymbols[0]).lastPrice);
    }
}
