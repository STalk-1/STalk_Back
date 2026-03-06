package com.stalk.api.kis.stock.controller;

import com.stalk.api.kis.stock.dto.StockRankingResponse;
import com.stalk.api.kis.stock.service.StockRankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[ver26.03.07]주식 랭킹 조회", description = "거래량 급등 TOP 10")
@RestController
@RequiredArgsConstructor
public class StockController {

    private final StockRankingService stockRankingService;

    @Operation(summary = "주식 랭킹 조회", description = "거래량 급등 TOP 10")
    @GetMapping("/stocks/top10")
    public StockRankingResponse getRanking() {
        return stockRankingService.getRanking();
    }

}
