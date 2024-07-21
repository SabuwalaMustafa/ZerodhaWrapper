package io.github.sabuwalamustafa.converters;

import io.github.sabuwalamustafa.models.brokermodels.Exchange;

public class ExchangeConverter {
    // todo: Verify correctness

    public static String toBrokerModel() {
        throw new RuntimeException(
                "ExchangeConverter.toBrokerModel() is not implemented yet");
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
