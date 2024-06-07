package io.github.sabuwalamustafa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

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

    public static JsonNode parseJsonStr(String jsonString)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonObj = objectMapper.readTree(jsonString);
        return jsonObj;
    }

    public static <T> T checkNotNull(T obj) {
        // Throws NullPointerException if obj is null, otherwise returns obj
        return Objects.requireNonNull(obj, "Passed object must not be null.");
    }
}
