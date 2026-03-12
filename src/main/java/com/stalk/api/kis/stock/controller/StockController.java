package com.stalk.api.kis.stock.controller;

import com.stalk.api.kis.stock.dto.FavoriteStockOverviewListResponse;
import com.stalk.api.kis.stock.dto.StockRankingResponse;
import com.stalk.api.kis.stock.service.StockOverviewService;
import com.stalk.api.kis.stock.service.StockRankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[ver26.03.12]주식 조회", description = "주식 데이터 조회")
@RestController
@RequiredArgsConstructor
public class StockController {

    private final StockRankingService stockRankingService;
    private final StockOverviewService stockOverviewService;

    @Operation(summary = "주식 랭킹 조회", description = "거래량 급등 TOP 10")
    @GetMapping("/stocks/top10")
    public StockRankingResponse getRanking() {
        return stockRankingService.getRanking();
    }

    @Operation(summary = "종목 목록 조회 (차트 제외)", description = "[관심종목 등록용]종목 전체 시세 조회 (차트 정보 제외)")
    @GetMapping("/stocks/overview")
    public ResponseEntity<FavoriteStockOverviewListResponse> getOverview() {
        return ResponseEntity.ok(stockOverviewService.getOverview());
    }

}
