package com.stalk.api.kis.stock.repository;

import com.stalk.api.kis.stock.FavoriteStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteStockRepository extends JpaRepository<FavoriteStock, Long> {
    List<FavoriteStock> findAllByUserId(Long userId);
    
    boolean existsByUserIdAndSymbol(Long userId, String symbol);
    
    void deleteByUserIdAndSymbol(Long userId, String symbol);
}
