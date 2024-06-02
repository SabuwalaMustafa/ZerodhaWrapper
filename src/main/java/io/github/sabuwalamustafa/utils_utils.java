package io.github.sabuwalamustafa;

public class utils_utils {
    public static String getNseSymbol(String symbol) {
        return "NSE:" + symbol;
    }

    public static String reverseNseSymbol(String nseSymbol) {
        String NSE_PREFIX = "NSE:";
        if (!nseSymbol.startsWith(NSE_PREFIX)) {
            throw new RuntimeException(
                    "NSE Symbol doesn't contain prefix `NSE:`");
        }
        return nseSymbol.substring("NSE:".length());
    }
}
