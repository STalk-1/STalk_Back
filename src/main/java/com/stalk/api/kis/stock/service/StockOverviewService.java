package com.stalk.api.kis.stock.service;

import com.stalk.api.kis.model.Direction;
import com.stalk.api.kis.stock.StockMaster;
import com.stalk.api.kis.stock.StockMasterProvider;
import com.stalk.api.kis.stock.StockProperties;
import com.stalk.api.kis.stock.dto.FavoriteStockOverviewListResponse;
import com.stalk.api.kis.stock.dto.FavoriteStockOverviewListResponse.FavoriteStockOverviewItem;
import com.stalk.api.kis.stock.dto.FavoriteStockOverviewListResponse.Quote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import static com.stalk.api.kis.model.DirectionMapper.fromKisSign;
import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static java.lang.Math.abs;
import static java.time.OffsetDateTime.ofInstant;

@RequiredArgsConstructor
@Service
public class StockOverviewService {

    private final StockProperties stockProperties;
    private final StockMasterProvider stockMasterProvider;
    private final StockPoller stockPoller;

    public FavoriteStockOverviewListResponse getOverview() {
        List<String> watchlist = stockProperties.watchlist();
        
        List<FavoriteStockOverviewItem> items = watchlist.stream().map(symbol -> {
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
                    OffsetDateTime asOf = ofInstant(cached.fetchedAt(), java.time.ZoneId.of("Asia/Seoul"));
                    quote = new Quote(name, market, price, change, changeRate, dir, asOf);
                } catch (Exception e) {
                    // ignore mapping errors
                }
            }

            return new FavoriteStockOverviewItem(symbol, quote, null);
        }).toList();

        return new FavoriteStockOverviewListResponse(items, items.size());
    }
}
