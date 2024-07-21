package io.github.sabuwalamustafa.converters;

import com.zerodhatech.models.Position;
import io.github.sabuwalamustafa.models.brokermodels.Exchange;
import io.github.sabuwalamustafa.models.brokermodels.PositionInternal;

public class ExchangeConverter {
    // todo: Verify correctness

    public static String toBrokerModel() {
        throw new RuntimeException(
                "PositionConverter.toBrokerModel() is not implemented yet");
    }

    public static Exchange toInternalModel(String exchange) {
        switch (exchange){
            case "NSE":
                return Exchange.NSE;
            case "BSE":
                return Exchange.BSE;
            default:
                throw new RuntimeException("Unexpected value of exchange found");
        }
    }
}
