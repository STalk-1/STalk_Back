package com.stalk.api.kis.stock.service;

import com.stalk.api.kis.stock.StockProperties;
import com.stalk.api.kis.stock.dto.KisInquirePriceResponse;
import com.stalk.api.websocket.stock.StockRealtimeMessageMapper;
import com.stalk.api.websocket.stock.StockRealtimePublisher;
import com.stalk.api.websocket.stock.dto.QuoteUpdatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
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
        log.info("[STOCK_POLLER] Initialized. watchlist={}", watchlist);
    }

    @Scheduled(fixedDelay=30000, initialDelayString = "5000")
    public void poll(){
        if (watchlist.isEmpty()) {
            log.warn("[STOCK_POLLER] Watchlist is empty. Check your 'quotes.watchlist' configuration.");
            return;
        }

        log.debug("[STOCK_POLLER] Starting stock polling for {} items...", watchlist.size());

        Instant fetchedAt = Instant.now();
        OffsetDateTime nowKst = OffsetDateTime.now(java.time.ZoneId.of("Asia/Seoul"));

        // 2페이지 차트 그리기 용도로 1분 단위의 기준 시점을 맞추기 위함(차트는 1분마다 점 하나씩 추가됨)
        int minute = nowKst.getMinute();
        OffsetDateTime activeInterval = nowKst.withMinute(minute).withSecond(0).withNano(0);
        boolean isNewInterval = currentInterval == null || activeInterval.isAfter(currentInterval);
        if (isNewInterval) {
            currentInterval = activeInterval;
        }

        LocalTime time = nowKst.toLocalTime();
        boolean isMarketHours = !time.isBefore(LocalTime.of(9, 0)) && !time.isAfter(LocalTime.of(15, 30));

        for (String code : watchlist) {
            try {
                var res = stockService.inquirePrice(code, "J");
                cache.put(code, new CachedStock(fetchedAt, res));

                QuoteUpdatedMessage message = messageMapper.toQuoteUpdatedMessage(code, fetchedAt, res);
                publisher.publishQuote(message);

                long currentPrice = Long.parseLong(res.output().currentPrice());
                List<CachedChartPoint> points = chartCache.get(code);

                /**
                 * chartCache 의 특정 code 에 해당하는 points 의 첫 데이터가 오늘 데이터가 아닐 결우 points를 버림
                 * 당일 데이터만 차트 점 데이터로 넘겨주기 위함
                 */
                if (points != null && !points.isEmpty()) {
                    OffsetDateTime firstPointDay = points.get(0).time();
                    if (!firstPointDay.toLocalDate().equals(nowKst.toLocalDate())) {
                        points.clear();
                    }
                }

                // 최초 실행 시 points 는 null 이기에 빈 리스트를 만들어줌
                if (points == null) {
                    points = new CopyOnWriteArrayList<>();
                    chartCache.put(code, points);
                }

                if (isNewInterval && isMarketHours) {
                    CachedChartPoint newPoint = new CachedChartPoint(activeInterval, currentPrice);
                    points.add(newPoint);
                    if (points.size() > 391) {
                        points.remove(0);
                    }
                    publisher.publishChartPointAdded(code, "1m", activeInterval, new BigDecimal(currentPrice));
                }

                log.debug("[STOCK_POLLER] Successfully polled and published code: {}", code);
            } catch (Exception e) {
                log.error("[STOCK_POLLER] Failed to poll code: {}. Error: {}", code, e.getMessage(), e);
            }
        }
        lastPolledAt = fetchedAt;
        log.debug("[STOCK_POLLER] Cycle finished. lastPolledAt: {}", lastPolledAt);
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

    public record CachedStock(
            Instant fetchedAt,
            KisInquirePriceResponse payload
    ) { }

}
