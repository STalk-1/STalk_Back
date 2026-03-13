package com.stalk.api.kis.market.controller;

import com.stalk.api.kis.market.dto.MarketResponse;
import com.stalk.api.kis.market.service.KisMarketService;
import com.stalk.api.kis.market.service.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "시장 지수", description = "메인 화면 시장 카드 조회 API")
@RestController
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @Operation(
            summary = "시장 지수 조회",
            description="KOSPI, NASDAQ, S&P500, USD/KRW 정보를 조회합니다."
    )
    @GetMapping("/market")
    public MarketResponse getMarket(){
        return marketService.getMarket();
    }
}
