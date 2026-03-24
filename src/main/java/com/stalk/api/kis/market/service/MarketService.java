package com.stalk.api.kis.market.service;


import com.stalk.api.kis.market.dto.MarketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MarketService {

    private final MarketPoller marketPoller;
    private final KisMarketService kisMarketService;

    public MarketService(
            MarketPoller marketPoller,
            KisMarketService kisMarketService
    ) {
        this.marketPoller = marketPoller;
        this.kisMarketService = kisMarketService;
    }

    public MarketResponse getMarket() {
        MarketPoller.CachedMarket cached = marketPoller.getCached();

        if (cached != null && cached.payload() != null) {
            log.info("[MARKET] Returning cached market data. fetchedAt={}", cached.fetchedAt());
            return cached.payload();
        }

        log.warn("[MARKET] Cached market data not found. Fetching directly from KIS...");
        return kisMarketService.getMarket();
    }
}