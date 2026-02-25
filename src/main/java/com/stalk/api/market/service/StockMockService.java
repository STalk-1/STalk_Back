package com.stalk.api.market.service;

import com.stalk.api.market.Domain.MarketIndexType;
import com.stalk.api.market.Domain.MarketType;
import com.stalk.api.market.Domain.Stock;
import com.stalk.api.market.dto.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;


@Service
public class StockMockService {

    // Mock: 즐겨찾기 코드 저장소 (실제로는 FavoriteStock 테이블)
    private final Set<String> favoriteCodes = ConcurrentHashMap.newKeySet();

    // Mock: 종목 마스터 (실제로는 stocks 테이블)
    private final Map<String, Stock> stockMaster = new HashMap<>();

    public StockMockService() {
        stockMaster.put("005930", Stock.builder().id(1L).name("삼성전자").code("005930").market(MarketType.KOSPI).build());
        stockMaster.put("000660", Stock.builder().id(2L).name("SK하이닉스").code("000660").market(MarketType.KOSPI).build());
        stockMaster.put("035420", Stock.builder().id(3L).name("NAVER").code("035420").market(MarketType.KOSPI).build());

        // 초기 즐겨찾기
        favoriteCodes.add("005930");
        favoriteCodes.add("000660");
    }

    public StockDashboardResponse getDashboard() {

        List<MarketIndexDto> marketIndices = List.of(
                MarketIndexDto.from(MarketIndexType.KOSPI, BigDecimal.valueOf(2507.01), BigDecimal.valueOf(15.21), BigDecimal.valueOf(0.61)),
                MarketIndexDto.from(MarketIndexType.NASDAQ, BigDecimal.valueOf(16234.55), BigDecimal.valueOf(-120.12), BigDecimal.valueOf(-0.74)),
                MarketIndexDto.from(MarketIndexType.SNP500, BigDecimal.valueOf(5123.44), BigDecimal.valueOf(10.12), BigDecimal.valueOf(0.20)),
                MarketIndexDto.from(MarketIndexType.USDKRW, BigDecimal.valueOf(1320.55), BigDecimal.valueOf(-5.12), BigDecimal.valueOf(-0.39))
        );

        List<StockRankDto> ranks = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> {
                    // Mock: 상위 종목은 일단 삼성전자로 통일
                    Stock entity = stockMaster.get("005930");

                    StockQuote quote = new StockQuote(
                            BigDecimal.valueOf(180000 + i * 100),
                            BigDecimal.valueOf(i % 2 == 0 ? -500 : 500),
                            BigDecimal.valueOf(i % 2 == 0 ? -0.62 : 0.62)
                    );

                    return StockRankDto.from(i, entity, quote);
                })
                .toList();

        return StockDashboardResponse.builder()
                .marketIndices(marketIndices)
                .topVolumeStocks(ranks)
                .build();
    }

    public List<StockCardDto> getFavoriteCards() {
        return favoriteCodes.stream()
                .map(code -> {
                    Stock entity = requireStock(code);
                    StockQuote quote = mockQuote(code);
                    List<BigDecimal> sparkline = mockSparkline(30, quote.price());
                    return StockCardDto.from(entity, quote, sparkline);
                })
                .toList();
    }

    public StockSparklineResponse getSparkline(String code) {
        StockQuote quote = mockQuote(code);
        List<BigDecimal> values = mockSparkline(30, quote.price());
        return StockSparklineResponse.of(code, 30, values);
    }

    public void addFavorite(String code) {
        requireStock(code);
        favoriteCodes.add(code);
    }

    public void removeFavorite(String code) {
        favoriteCodes.remove(code);
    }

    private Stock requireStock(String code) {
        Stock entity = stockMaster.get(code);
        if (entity == null) {
            throw new IllegalArgumentException("Unknown stock code: " + code);
        }
        return entity;
    }

    private StockQuote mockQuote(String code) {
        // code 기반으로 변동을 조금 주는 간단 Mock
        int seed = Math.abs(code.hashCode() % 1000);

        BigDecimal price = BigDecimal.valueOf(50000 + seed * 10L);
        BigDecimal changeValue = (seed % 2 == 0)
                ? BigDecimal.valueOf(300)
                : BigDecimal.valueOf(-300);

        BigDecimal changeRate = changeValue
                .multiply(BigDecimal.valueOf(100))
                .divide(price, 2, java.math.RoundingMode.HALF_UP);

        return new StockQuote(price, changeValue, changeRate);
    }

    private List<BigDecimal> mockSparkline(int days, BigDecimal lastPrice) {
        // 과거 -> 현재 순. lastPrice 근처에서 랜덤 워크
        Random r = new Random(lastPrice.longValue());
        List<BigDecimal> values = new ArrayList<>(days);

        BigDecimal v = lastPrice.subtract(BigDecimal.valueOf(days * 50L));
        for (int i = 0; i < days; i++) {
            int step = r.nextInt(200) - 100; // -100 ~ +99
            v = v.add(BigDecimal.valueOf(step));
            if (v.compareTo(BigDecimal.ZERO) < 0) v = BigDecimal.ONE;
            values.add(v);
        }
        // 마지막 값을 현재가로 맞춤(시각적으로 자연)
        values.set(values.size() - 1, lastPrice);
        return values;
    }
}
