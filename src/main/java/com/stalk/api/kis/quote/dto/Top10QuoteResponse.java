package com.stalk.api.kis.quote.dto;

import java.time.Instant;
import java.util.List;

public record Top10QuoteResponse(
        String title,               // 거래량 급등 TOP 10
        Instant lastPolledAt,
        List<Top10QuoteItem> items
) {
}
