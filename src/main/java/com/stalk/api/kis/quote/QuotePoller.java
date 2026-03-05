package com.stalk.api.kis.quote;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class QuotePoller {

    private final KisDomesticQuoteService quoteService;
    private final List<String> watchlist;
    private final boolean enabled;
    private volatile Instant lastPolledAt;


    private final Map<String, CachedQuote> cache = new ConcurrentHashMap<>();

    public QuotePoller(
            KisDomesticQuoteService quoteService,
            QuoteProperties properties
    ) {
        this.quoteService = quoteService;
        this.watchlist = properties.watchlist();
        this.enabled = properties.poll().enabled();
        log.info("QuotePoller initialized. watchlist={}, enabled={}", watchlist, enabled);
    }

    @Scheduled(fixedDelay=30000, initialDelayString = "2000")
    public void poll(){
        if (!enabled) {
            log.info("QuotePoller is disabled. skipping...");
            return;
        }

        if (watchlist.isEmpty()) {
            log.warn("Watchlist is empty. Check your 'quotes.watchlist' configuration.");
            return;
        }

        log.info("Starting quote polling for {} items...", watchlist.size());
        Instant fetchedAt = Instant.now();
        for (String code : watchlist) {
            try {
                var res = quoteService.inquirePrice(code, "J");
                cache.put(code, new CachedQuote(fetchedAt, res));
                log.info("Successfully polled code: {}", code);
            } catch (Exception e) {
                log.error("Failed to poll code: {}. Error: {}", code, e.getMessage());
            }
        }
        lastPolledAt = fetchedAt;
        log.info("QuotePoller cycle finished.lastPolledAt: {}", lastPolledAt);
    }

    public CachedQuote getCached(String code) {
        return cache.get(code);
    }

    public Instant getLastPolledAt() {
        return lastPolledAt;
    }

    public record CachedQuote(Instant fetchedAt, KisDomesticQuoteService.InquirePriceResponse payload) { }

}
