package io.github.sabuwalamustafa.converters;

import com.zerodhatech.models.Holding;
import io.github.sabuwalamustafa.models.brokermodels.Asset;
import io.github.sabuwalamustafa.models.brokermodels.Exchange;
import io.github.sabuwalamustafa.models.brokermodels.HoldingInternal;

public class HoldingConverter {
    public static Holding toBrokerModel() {
        throw new RuntimeException(
                "HoldingConverter.toBrokerModel() is not implemented yet");
    }

    public static HoldingInternal toInternalModel(Holding holding) {
        HoldingInternal.HoldingInternalBuilder holdingInternalBuilder
                = HoldingInternal.builder();
        holdingInternalBuilder.asset(Asset.builder().symbol(
                                      holding.tradingSymbol).exchange(
                                      ExchangeConverter.toInternalModel(holding.exchange)).build())
                              .quantity(holding.quantity)
                              .t1quantity(holding.t1Quantity)
                              .averagePrice(holding.averagePrice);
        return holdingInternalBuilder.build();
    }
}
