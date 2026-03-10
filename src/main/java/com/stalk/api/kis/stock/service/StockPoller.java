package com.stalk.api.kis.stock.service;

import com.stalk.api.kis.stock.StockProperties;
import com.stalk.api.kis.stock.dto.KisInquirePriceResponse;
import com.stalk.api.websocket.stock.StockRealtimeMessageMapper;
import com.stalk.api.websocket.stock.StockRealtimePublisher;
import com.stalk.api.websocket.stock.dto.QuoteUpdatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Collections.emptyList;

@Slf4j
@Component
public class StockPoller {

    private final KisDomesticStockService stockService;
    private final List<String> watchlist;
    private volatile Instant lastPolledAt;
    private final StockRealtimePublisher publisher;
    private final StockRealtimeMessageMapper messageMapper;

    private final Map<String, CachedStock> cache = new ConcurrentHashMap<>();
    private final Map<String, List<CachedChartPoint>> chartCache = new ConcurrentHashMap<>();
    private volatile OffsetDateTime currentInterval;

    public record CachedChartPoint(OffsetDateTime time, long close) {}

    public StockPoller(
            KisDomesticStockService stockService,
            StockProperties properties,
            StockRealtimePublisher publisher,
            StockRealtimeMessageMapper messageMapper
    ) {
        this.stockService = stockService;
        this.watchlist = properties.watchlist();
        this.publisher = publisher;
        this.messageMapper = messageMapper;
        log.info("StockPoller initialized. watchlist={}", watchlist);
    }

    @Scheduled(fixedDelay=10000, initialDelayString = "5000")
    public void poll(){
        if (watchlist.isEmpty()) {
            log.warn("Watchlist is empty. Check your 'quotes.watchlist' configuration.");
            return;
        }

        log.info("Starting stock polling for {} items...", watchlist.size());

        Instant fetchedAt = Instant.now();
        OffsetDateTime nowKst = OffsetDateTime.now(java.time.ZoneId.of("Asia/Seoul"));

        // 2페이지 차트 그리기 용도로 30분 단위의 기준 시점을 맞추기 위함(차트는 30분마다 점 하나씩 추가됨)
        int minute = (nowKst.getMinute() / 30) * 30;
        OffsetDateTime activeInterval = nowKst.withMinute(minute).withSecond(0).withNano(0);
        boolean isNewInterval = currentInterval == null || activeInterval.isAfter(currentInterval);
        if (isNewInterval) {
            currentInterval = activeInterval;
        }

        for (String code : watchlist) {
            try {
                var res = stockService.inquirePrice(code, "J");
                cache.put(code, new CachedStock(fetchedAt, res));

                QuoteUpdatedMessage message = messageMapper.toQuoteUpdatedMessage(code, fetchedAt, res);
                publisher.publishQuote(message);

                long currentPrice = Long.parseLong(res.output().currentPrice());
                List<CachedChartPoint> points = chartCache.get(code);

                if (points == null) {
                    // 초기 mock data 생성 후 리스트에 추가
                    points = seedChartCache(activeInterval, currentPrice);
                    chartCache.put(code, points);
                } else if (isNewInterval) {
                    CachedChartPoint newPoint = new CachedChartPoint(activeInterval, currentPrice);
                    points.add(newPoint);
                    if (points.size() > 20) {
                        points.remove(0);
                    }
                    publisher.publishChartPointAdded(code, "30m", activeInterval, new java.math.BigDecimal(currentPrice));
                }

                log.info("Successfully stock polled code and publish to ws: {}", code);
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

    public List<CachedChartPoint> getChartPoints(String code) {
        return chartCache.getOrDefault(code, emptyList());
    }

    public OffsetDateTime getCurrentInterval() {
        return currentInterval;
    }

    private List<CachedChartPoint> seedChartCache(OffsetDateTime activeInterval, long currentPrice) {
        List<CachedChartPoint> points = new CopyOnWriteArrayList<>();
        for (int i = 19; i >= 0; i--) {
            OffsetDateTime time = activeInterval.minusMinutes(30L * i);
            long price = currentPrice - (i * 500L); // Mock fluctuation
            points.add(new CachedChartPoint(time, price));
        }
        return points;
    }

    public record CachedStock(
            Instant fetchedAt,
            KisInquirePriceResponse payload
    ) { }

}
