package com.stalk.api.kis.stock.service;

import com.stalk.api.kis.stock.FavoriteStock;
import com.stalk.api.kis.stock.StockMasterProvider;
import com.stalk.api.kis.stock.repository.FavoriteStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FavoriteStockService {

    private final FavoriteStockRepository favoriteStockRepository;
    private final StockMasterProvider stockMasterProvider;

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
}
