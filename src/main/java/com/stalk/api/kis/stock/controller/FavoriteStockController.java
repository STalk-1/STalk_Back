package com.stalk.api.kis.stock.controller;

import com.stalk.api.auth.security.CustomPrincipal;
import com.stalk.api.kis.stock.dto.FavoriteStockOverviewListResponse;
import com.stalk.api.kis.stock.service.FavoriteStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "관심 종목 조회", description = "관심 종목 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/stocks/favorites")
public class FavoriteStockController {

    private final FavoriteStockService favoriteStockService;

    @Operation(summary = "관심 종목 등록", description = "종목 코드를 통해 관심 종목을 등록합니다.")
    @PostMapping("/{symbol}")
    public ResponseEntity<Void> addFavoriteStock(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Parameter(description = "종목 코드 (예: 005930)", example = "005930") @PathVariable String symbol) {
        favoriteStockService.addFavoriteStock(principal.userId(), symbol);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관심 종목 해제", description = "등록된 관심 종목을 해제합니다.")
    @DeleteMapping("/{symbol}")
    public ResponseEntity<Void> removeFavoriteStock(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Parameter(description = "종목 코드 (예: 005930)", example = "005930") @PathVariable String symbol) {
        favoriteStockService.removeFavoriteStock(principal.userId(), symbol);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관심 종목 조회", description = "등록된 관심 종목 목록과 요약 차트 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<FavoriteStockOverviewListResponse> getFavoriteStocks(
            @AuthenticationPrincipal CustomPrincipal principal) {
        var response = favoriteStockService.getFavoriteStocks(principal.userId());
        return ResponseEntity.ok(response);
    }
}
