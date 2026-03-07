package com.stalk.api.websocket.stock;

import com.stalk.api.kis.model.Direction;
import com.stalk.api.kis.model.DirectionMapper;
import com.stalk.api.kis.stock.dto.KisInquirePriceResponse;
import com.stalk.api.websocket.stock.dto.QuoteUpdatedMessage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
public class StockRealtimeMessageMapper {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public QuoteUpdatedMessage toQuoteUpdatedMessage(
            String symbol,
            Instant fetchedAt,
            KisInquirePriceResponse response
    ) {
        KisInquirePriceResponse.Output output = response.output();

        BigDecimal price = toBigDecimal(output.currentPrice());
        BigDecimal change = toBigDecimal(output.change());
        BigDecimal changeRate = toBigDecimal(output.changeRate());
        Direction direction = DirectionMapper.fromKisSign(output.sign());

        OffsetDateTime asOf = OffsetDateTime.ofInstant(fetchedAt, KST);

        return QuoteUpdatedMessage.of(
                symbol,
                price,
                change,
                changeRate,
                direction,
                asOf
        );
    }

    private BigDecimal toBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.trim()).abs();
    }
}
