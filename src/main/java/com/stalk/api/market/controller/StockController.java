package com.stalk.api.market.controller;

import com.stalk.api.global.ApiResponse;
import com.stalk.api.market.dto.*;
import com.stalk.api.market.service.StockMockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stock")
public class StockController {

    private final StockMockService stockMockService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<StockDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(stockMockService.getDashboard()));
    }

    // 즐겨찾기 카드 리스트
    @GetMapping("/favorites/cards")
    public ResponseEntity<ApiResponse<java.util.List<StockCardDto>>> getFavoriteCards() {
        return ResponseEntity.ok(ApiResponse.ok(stockMockService.getFavoriteCards()));
    }

    // 즐겨찾기 추가
    @PostMapping("/favorites/{code}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(@PathVariable String code) {
        stockMockService.addFavorite(code);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 즐겨찾기 삭제 (하트 누르면 사라짐)
    @DeleteMapping("/favorites/{code}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(@PathVariable String code) {
        stockMockService.removeFavorite(code);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 스파크라인 단독 조회
    @GetMapping("/{code}/sparkline")
    public ApiResponse<StockSparklineResponse> getSparkline(
            @PathVariable String code,
            @RequestParam(defaultValue = "30") int days
    ) {
        return ApiResponse.ok(stockMockService.getSparkline(code, days));
    }

}
