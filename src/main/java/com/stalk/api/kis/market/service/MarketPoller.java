package com.stalk.api.kis.market.service;


import com.stalk.api.kis.market.dto.MarketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class MarketPoller {

    private final KisMarketService kisMarketService;

    private volatile CachedMarket cachedMarket;
    private volatile Instant lastPolledAt;

    public MarketPoller(KisMarketService kisMarketService) {
        this.kisMarketService = kisMarketService;
        log.info("[MARKET] MarketPoller initialized.");
    }

    @Scheduled(fixedDelay = 30000, initialDelayString = "5000")
    public void poll() {
        log.info("[MARKET] Starting market polling...");
        Instant fetchedAt = Instant.now();

        try {
            MarketResponse response = kisMarketService.getMarket();
            cachedMarket = new CachedMarket(fetchedAt, response);
            lastPolledAt = fetchedAt;

            log.info("[MARKET] Market polling finished successfully. lastPolledAt={}", lastPolledAt);
        } catch (Exception e) {
            log.error("[MARKET] Failed to poll market data. Error: {}", e.getMessage(), e);
        }
    }

    public CachedMarket getCached() {
        return cachedMarket;
    }

    public Instant getLastPolledAt() {
        return lastPolledAt;
    }

    public record CachedMarket(
            Instant fetchedAt,
            MarketResponse payload
    ) {}
}