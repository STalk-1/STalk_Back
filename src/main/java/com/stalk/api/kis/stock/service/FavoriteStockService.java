package com.stalk.api.kis.stock.service;

import com.stalk.api.kis.model.Direction;
import com.stalk.api.kis.stock.FavoriteStock;
import com.stalk.api.kis.stock.StockMasterProvider;
import com.stalk.api.kis.stock.repository.FavoriteStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stalk.api.kis.stock.dto.FavoriteStockOverviewListResponse;
import com.stalk.api.kis.stock.dto.FavoriteStockOverviewListResponse.Chart;
import com.stalk.api.kis.stock.dto.FavoriteStockOverviewListResponse.ChartPoint;
import com.stalk.api.kis.stock.dto.FavoriteStockOverviewListResponse.FavoriteStockOverviewItem;
import com.stalk.api.kis.stock.dto.FavoriteStockOverviewListResponse.Quote;
import com.stalk.api.kis.stock.StockMaster;

import java.time.OffsetDateTime;
import java.util.List;

import static com.stalk.api.kis.model.DirectionMapper.fromKisSign;
import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static java.lang.Math.abs;

@RequiredArgsConstructor
@Service
public class FavoriteStockService {

    private final FavoriteStockRepository favoriteStockRepository;
    private final StockMasterProvider stockMasterProvider;
    private final StockPoller stockPoller;

    @Transactional
    public void addFavoriteStock(Long userId, String symbol) {
        // Validate stock symbol exists
        if (stockMasterProvider.getStockMasterFromCode(symbol) == null) {
            throw new IllegalArgumentException("지원하지 않는 주식 종목 코드입니다: " + symbol);
        }

        // Check if already favorited
        if (favoriteStockRepository.existsByUserIdAndSymbol(userId, symbol)) {
            return;
        }

        // Save
        FavoriteStock favoriteStock = new FavoriteStock(userId, symbol);
        favoriteStockRepository.save(favoriteStock);
    }

    @Transactional
    public void removeFavoriteStock(Long userId, String symbol) {
        favoriteStockRepository.deleteByUserIdAndSymbol(userId, symbol);
    }

    @Transactional(readOnly = true)
    public FavoriteStockOverviewListResponse getFavoriteStocks(Long userId) {
        List<FavoriteStock> favorites = favoriteStockRepository.findAllByUserId(userId);

        List<FavoriteStockOverviewItem> items = favorites.stream().map(fav -> {
            String symbol = fav.getSymbol();

            StockMaster master = stockMasterProvider.getStockMasterFromCode(symbol);
            String name = master != null ? master.name() : symbol;
            String market = master != null ? master.market() : "UNKNOWN";

            StockPoller.CachedStock cached = stockPoller.getCached(symbol);
            Quote quote = null;
            if (cached != null) {
                var output = cached.payload().output();
                try {
                    long price = parseLong(output.currentPrice());
                    long change = abs(parseLong(output.change()));
                    double changeRate = parseDouble(output.changeRate());
                    Direction dir = fromKisSign(output.sign());
                    OffsetDateTime asOf = OffsetDateTime.ofInstant(cached.fetchedAt(), java.time.ZoneId.of("Asia/Seoul"));
                    quote = new Quote(name, market, price, change, changeRate, dir, asOf);
                } catch (Exception e) {
                    // ignore mapping errors
                }
            }

            List<StockPoller.CachedChartPoint> cachedPoints = stockPoller.getChartPoints(symbol);
            List<ChartPoint> chartPoints = cachedPoints.stream()
                    .map(p -> new ChartPoint(p.time(), p.close()))
                    .toList();

            OffsetDateTime chartAsOf = stockPoller.getCurrentInterval();
            if (chartAsOf == null) {
                chartAsOf = OffsetDateTime.now(java.time.ZoneId.of("Asia/Seoul"));
            }
            Chart chart = new Chart("1m", chartPoints, chartAsOf);

            return new FavoriteStockOverviewItem(symbol, quote, chart);
        }).toList();

        return new FavoriteStockOverviewListResponse(items, items.size());
    }
}
