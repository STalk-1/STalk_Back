package com.stalk.api.kis.quote.dto;

import java.time.Instant;

public record Top10QuoteItem(
        int rank,
        String code,
        String name,
        String market,      // KOSPI

        Long price,         // 현재가
        Long diff,          // 전일대비
        Double diffRate,    // 등락률(%)

        Instant fetchedAt,
        boolean stale
) {
}
