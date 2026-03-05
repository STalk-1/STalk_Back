package com.stalk.api.kis.quote;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "국내 주식 시세", description = "한국투자증권 API를 이용한 국내 주식 시세 조회 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/quotes")
public class QuoteController {

    private final KisDomesticQuoteService quoteService;
    private final QuotePoller poller;

    @Operation(
            summary = "국내 주식 현재가 조회",
            description = "종목 코드와 시장 구분 코드를 사용하여 해당 주식의 현재가, 전일 대비, 거래량 등을 조회합니다."
    )
    @GetMapping("/{code}")
    public KisDomesticQuoteService.InquirePriceResponse quote(
            @PathVariable String code,
            @RequestParam(defaultValue = "J") String marketDiv
    ) {
        return quoteService.inquirePrice(code, marketDiv);
    }

    @Operation(
            summary = "국내 주식 현재가 캐시 조회",
            description = "종목 코드와 시장 구분 코드를 사용하여 해당 주식의 현재가, 전일 대비, 거래량 등을 조회합니다."
    )
    @GetMapping("/cached/{code}")
    public QuotePoller.CachedQuote cached(@PathVariable String code) {
        return poller.getCached(code);
    }
}
