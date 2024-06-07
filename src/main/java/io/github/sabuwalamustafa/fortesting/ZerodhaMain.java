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
import io.github.sabuwalamustafa.models.ResponseWrapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ZerodhaMain {
    private static final String HDFCBANK = "HDFCBANK";
    private static String zerodhaApi = "";
    private static String zerodhaUserId = "";

    public static void main(String[] args) throws IOException {
        FileUtils fileUtils = FileUtils.getInstance();
        ILogStuff logStuff = LogStuff.getInstance(fileUtils, null);

        String googleCloudKeyFilePath = "google_cloud_key.json";
        ServiceAccountCredentials
                credential = ServiceAccountCredentials.fromStream(
                ResourcesFileUtils.getInstance()
                                  .getInputStream(
                                          GFSFileUtils.class,
                                          googleCloudKeyFilePath));

        ZerodhaUtils zerodhaUtils = ZerodhaUtils.getInstance(logStuff,
                                                             Map.of("zerodha_api",
                                                                    zerodhaApi,
                                                                    "zerodha_user_id",
                                                                    zerodhaUserId),
                                                             credential);

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

        ResponseWrapper<List<HistoricalData>> rw
                = zerodhaUtils.getHistoricalData(HDFCBANK,
                                                 "2024-06-05 09:30:00",
                                                 "2024-06-06 15:30:00");
        rw.getTResponse().stream().forEach(v -> {
            System.out.println(v.timeStamp + " :: " + v.close);
        });
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
