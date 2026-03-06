package com.stalk.api.kis.stock.service;

import com.stalk.api.kis.stock.StockProperties;
import com.stalk.api.kis.stock.dto.KisInquirePriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class StockPoller {

    private final KisDomesticStockService stockService;
    private final List<String> watchlist;
    private volatile Instant lastPolledAt;

    private final Map<String, CachedStock> cache = new ConcurrentHashMap<>();

    public StockPoller(
            KisDomesticStockService stockService,
            StockProperties properties
    ) {
        this.stockService = stockService;
        this.watchlist = properties.watchlist();
        log.info("StockPoller initialized. watchlist={}", watchlist);
    }

    @Scheduled(fixedDelay=30000, initialDelayString = "5000")
    public void poll(){
        if (watchlist.isEmpty()) {
            log.warn("Watchlist is empty. Check your 'quotes.watchlist' configuration.");
            return;
        }

        log.info("Starting stock polling for {} items...", watchlist.size());
        Instant fetchedAt = Instant.now();
        for (String code : watchlist) {
            try {
                var res = stockService.inquirePrice(code, "J");
                cache.put(code, new CachedStock(fetchedAt, res));
                log.info("Successfully stock polled code: {}", code);
            } catch (Exception e) {
                log.error("Failed to poll code: {}. Error: {}", code, e.getMessage());
            }
        }
        lastPolledAt = fetchedAt;
        log.info("QuotePoller cycle finished.lastPolledAt: {}", lastPolledAt);
    }

    public CachedStock getCached(String code) {

        return cache.get(code);
    }

    public Instant getLastPolledAt() {

        return lastPolledAt;
    }

    public record CachedStock(
            Instant fetchedAt,
            KisInquirePriceResponse payload
    ) { }

}
