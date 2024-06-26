package io.github.sabuwalamustafa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sabuwalamustafa.models.OrderStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class utils_utils {
    private static final String FILE_PATH_DELIMITER = System.getProperty(
            "file.separator");

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

    // todo: Probably don't need delimiterSuffix.
    public static String buildPath(boolean delimiterSuffix, String... argv) {
        StringBuilder path = new StringBuilder();
        for (String arg : argv) {
            if (path.length() > 0) {
                path.append(FILE_PATH_DELIMITER);
            }
            path.append(arg);
        }
        if (delimiterSuffix) {
            path.append(FILE_PATH_DELIMITER);
        }
        return path.toString();
    }

    public static boolean isTerminalStatus(OrderStatus status) {
        return List.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED,
                       OrderStatus.REJECTED).contains(status);
    }

    public static Map<String, String> parseCmdLine(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (String arg : args) {
            String[] sp = arg.substring("--".length()).split("=", 2);
            map.put(sp[0], sp[1]);
        }
        return map;
    }
}
