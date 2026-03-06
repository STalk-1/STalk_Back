package com.stalk.api.kis.stock.service;

import com.stalk.api.kis.model.DirectionMapper;
import com.stalk.api.kis.stock.StockProperties;
import com.stalk.api.kis.stock.StockMasterProvider;
import com.stalk.api.kis.stock.dto.StockRankingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class StockRankingService {

    private final StockPoller poller;
    private final StockMasterProvider stockMasterProvider;
    private final List<String> watchlist;

    public StockRankingService(
            StockPoller poller,
            StockMasterProvider stockMasterProvider,
            StockProperties properties
    ) {
        this.poller = poller;
        this.stockMasterProvider = stockMasterProvider;
        this.watchlist = properties.watchlist();

        log.info("StockRankingService initialized. watchlistSize={}, watchlist={}", watchlist.size(), watchlist);
    }

    public StockRankingResponse getRanking() {

        Instant lastPolledAtInstant = poller.getLastPolledAt();

        // Instant를 한국 시간(+09:00) OffsetDateTime으로 변환
        //.env에 설정한 TZ=Asia/Seoul과 일치
        OffsetDateTime lastPolledAt = (lastPolledAtInstant != null)
                ? lastPolledAtInstant.atOffset(java.time.ZoneOffset.ofHours(9))
                : OffsetDateTime.now(); // 폴링 기록이 없으면 현재 시각

        log.info("StockRankingService.getRanking called. lastPolledAt={}", lastPolledAt);

        List<StockRankingResponse.StockRankingItem> items = new ArrayList<>();

        for (int i = 0; i < watchlist.size(); i++) {
            String code = watchlist.get(i);

            var master = stockMasterProvider.getStockMasterFromCode(code);
            var cachedStock = poller.getCached(code);

            if (cachedStock == null) continue; // 데이터 없으면 패스

            var output = cachedStock.payload().output();

            items.add(new StockRankingResponse.StockRankingItem(
                    i+1,
                    code,
                    master.name(),
                    master.market(),
                    toBigDecimal(output.currentPrice()),
                    toBigDecimal(output.change()),
                    toBigDecimal(output.changeRate()),
                    DirectionMapper.fromKisSign(output.sign())
            ));
        }

        log.info("Top10VolumeService response built. itemCount={}, lastPolledAt={}", items.size(), lastPolledAt);

        return new StockRankingResponse("거래량 급등 TOP 10", lastPolledAt, items, items.size());
    }


    private BigDecimal toBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.trim()).abs();
    }
}
