package com.stalk.api.kis.quote;

import com.stalk.api.kis.quote.dto.Top10QuoteItem;
import com.stalk.api.kis.quote.dto.Top10QuoteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class Top10VolumeService {

    private final QuotePoller poller;
    private final StockMasterProvider stockMasterProvider;
    private final List<String> watchlist;


    // stale 기준(5분)
    private static final Duration STALE_THRESHOLD = Duration.ofMinutes(5);

    public Top10VolumeService(
            QuotePoller poller,
            StockMasterProvider stockMasterProvider,
            QuoteProperties properties
    ) {
        this.poller = poller;
        this.stockMasterProvider = stockMasterProvider;
        this.watchlist = properties.watchlist();

        log.info("Top10VolumeService initialized. watchlistSize={}, watchlist={}", watchlist.size(), watchlist);
    }

    public Top10QuoteResponse getTop10() {
        Instant now = Instant.now();
        Instant lastPolledAt = poller.getLastPolledAt();

        log.info("Top10VolumeService.getTop10 called. lastPolledAt={}", lastPolledAt);

        List<Top10QuoteItem> items = new ArrayList<>();
        for (int i = 0; i < watchlist.size(); i++) {
            int rank = i + 1;
            String code = watchlist.get(i);

            var master = stockMasterProvider.getOrNull(code);
            String name = master != null ? master.name() : code;
            String market = master != null ? master.market() : null;

            var cached = poller.getCached(code);

            Long price = null;
            Long diff = null;
            Double diffRate = null;
            Instant fetchedAt = null;
            boolean stale = true;

            if (cached == null) {
                log.debug("Cache miss. code={}", code);
            }

            if (cached != null && cached.payload() != null && cached.payload().output() != null) {
                fetchedAt = cached.fetchedAt();
                stale = fetchedAt == null || fetchedAt.isBefore(now.minus(STALE_THRESHOLD));

                // KIS 응답은 문자열이 많아서 안전 파싱
                var out = cached.payload().output();
                price = parseLong(out.currentPrice());
                diff = parseLong(out.diff());
                diffRate = parseDouble(out.diffRate());
            }

            items.add(new Top10QuoteItem(
                    rank,
                    code,
                    name,
                    market,
                    price,
                    diff,
                    diffRate,
                    fetchedAt,
                    stale
            ));
        }
        log.info("Top10VolumeService response built. itemCount={}, lastPolledAt={}", items.size(), lastPolledAt);

        return new Top10QuoteResponse("거래량 급등 TOP 10", lastPolledAt, items);
    }

    private Long parseLong(String s) {
        try {
            return s == null ? null : Long.parseLong(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(String s) {
        try {
            return s == null ? null : Double.parseDouble(s.trim());
        } catch (Exception e) {
            return null;
        }


    }
}
