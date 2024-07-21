package io.github.sabuwalamustafa.converters;

import com.zerodhatech.models.Position;
import io.github.sabuwalamustafa.models.brokermodels.Asset;
import io.github.sabuwalamustafa.models.brokermodels.Exchange;
import io.github.sabuwalamustafa.models.brokermodels.PositionInternal;

public class PositionConverter {
    public static Position toBrokerModel() {
        throw new RuntimeException(
                "PositionConverter.toBrokerModel() is not implemented yet");
    }

    public static PositionInternal toInternalModel(Position position) {
        PositionInternal.PositionInternalBuilder positionInternalBuilder
                = PositionInternal.builder();
        positionInternalBuilder.asset(Asset.builder().exchange(
                                                   ExchangeConverter.toInternalModel(position.exchange))
                                           .symbol(
                                                   position.tradingSymbol)
                                           .build()).netQuantity(
                                       position.netQuantity)
                               .buyQuantity(position.buyQuantity).sellQuantity(
                                       position.sellQuantity)
                               .buyAveragePrice(position.buyPrice)
                               .sellAveragePrice(position.sellPrice)
                               .netAveragePrice(position.averagePrice);
        // todo: check if average price is correct here
        return positionInternalBuilder.build();
    }
}
