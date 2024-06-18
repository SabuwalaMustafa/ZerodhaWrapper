package io.github.sabuwalamustafa.fortesting;


import com.google.api.client.auth.oauth2.Credential;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.zerodhatech.models.HistoricalData;
import io.github.sabuwalamustafa.FilePathsProvider;
import io.github.sabuwalamustafa.GFSFileUtils;
import io.github.sabuwalamustafa.ZerodhaUtils;
import io.github.sabuwalamustafa.interfaces.IFilePathsProvider;
import io.github.sabuwalamustafa.interfaces.IFileUtils;
import io.github.sabuwalamustafa.interfaces.ILogStuff;
import io.github.sabuwalamustafa.models.OrderInternal;
import io.github.sabuwalamustafa.models.ResponseWrapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class ZerodhaMain {
    private static final String HDFCBANK = "HDFCBANK";

    public static void main(String[] args) throws IOException {
        String zerodhaApi = args[0].split("=", 2)[1];
        String zerodhaUserId = args[1].split("=", 2)[1];
        FileUtils fileUtils = FileUtils.getInstance();
        ILogStuff logStuff = LogStuff.getInstance(fileUtils, null);

//        String googleCloudKeyFilePath = "google_cloud_key.json";
//        InputStream inputStream = ResourcesFileUtils.getInstance()
//                                                    .getInputStream(
//                                  GFSFileUtils.class,
//                                  googleCloudKeyFilePath);
        // Use the input stream as needed (e.g., read its content)
        String googleCloudKeyFilePath
                = "D:\\Interview prep 2023\\Config\\google_cloud_key_0.json";
        InputStream inputStream = new FileInputStream(googleCloudKeyFilePath);
//        try (Scanner scanner = new Scanner(inputStream)) {
//            while (scanner.hasNextLine()) {
//                System.out.println(scanner.nextLine());
//            }
//        }
        ServiceAccountCredentials
                credential = ServiceAccountCredentials.fromStream(
                inputStream);

        ZerodhaUtils zerodhaUtils = ZerodhaUtils.getInstance(logStuff,
                                                             Map.of("zerodha_api",
                                                                    zerodhaApi,
                                                                    "zerodha_user_id",
                                                                    zerodhaUserId),
                                                             credential);

        ResponseWrapper<Double> fundsAvailableRW
                = zerodhaUtils.getFundsAvailable();
        System.out.println(fundsAvailableRW.getTResponse());

//        String symbol = "RELIANCE";
//        System.out.println(zerodhaUtils.getLtp(symbol).getTResponse());

//        String[] symbols = new String[]{"RELIANCE", "HINDUNILVR", "PAGEIND"};
//        System.out.println(zerodhaUtils.getLtpSymbolList(List.of(symbols)));


//        System.out.println(zerodhaUtils.placeBuyOrder("ITBEES", 1, 34.99));

//        System.out.println(zerodhaUtils.placeSellOrder("ITBEES", 1, 34.99));

//        ResponseWrapper<OrderInternal> oi = zerodhaUtils.getOrderDetails(
//                "");
//        System.out.println(oi.toString());

//        System.out.println(zerodhaUtils.getLtp("HDFCBANK").getTResponse());

//        ResponseWrapper<List<HistoricalData>> rw
//                = zerodhaUtils.getHistoricalData(HDFCBANK,
//                                                 "2024-06-05 09:30:00",
//                                                 "2024-06-06 15:30:00");
//        rw.getTResponse().stream().forEach(v -> {
//            System.out.println(v.timeStamp + " :: " + v.close);
//        });

//        ResponseWrapper<List<OrderInternal>> rw
//                = zerodhaUtils.getAllOrders(HDFCBANK, null);
//        System.out.println(rw.getTResponse().size());
//        rw.getTResponse().stream().forEach(System.out::println);
    }

    private static Date getIstDate(String strTimestamp) {
        String timestamp = "2024-06-07 14:45:30";

        // Define the date format corresponding to the timestamp string
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        // Set the timezone to IST
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date date = null;
        try {
            date = dateFormat.parse(strTimestamp);
        } catch (ParseException e) {
            // todo
        }
        return date;
    }
}
