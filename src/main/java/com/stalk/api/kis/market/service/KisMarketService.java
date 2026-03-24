package com.stalk.api.kis.market.service;

import com.stalk.api.kis.KisProperties;
import com.stalk.api.kis.KisTokenService;
import com.stalk.api.kis.market.MarketProperties;
import com.stalk.api.kis.market.dto.KisDomesticIndexPriceResponse;
import com.stalk.api.kis.market.dto.KisOverseasChartPriceResponse;
import com.stalk.api.kis.market.dto.MarketResponse;
import com.stalk.api.kis.model.DirectionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class KisMarketService {

    private static final String KOSPI_NAME = "KOSPI";
    private static final String NASDAQ_NAME = "NASDAQ";
    private static final String SP500_NAME = "S&P500";
    private static final String USD_KRW_NAME = "USD/KRW";

    private final RestClient kisRestClient;
    private final KisProperties props;
    private final KisTokenService tokenService;
    private final MarketProperties marketProperties;

    public KisMarketService(
            RestClient kisRestClient,
            KisProperties props,
            KisTokenService tokenService,
            MarketProperties marketProperties
    ) {
        this.kisRestClient = kisRestClient;
        this.props = props;
        this.tokenService = tokenService;
        this.marketProperties = marketProperties;
    }

    public MarketResponse getMarket() {
        log.info("[MARKET_KIS] 시장 지표 종합 조회 시작");
        List<MarketResponse.MarketItem> markets = List.of(
                inquireKospi(),
                inquireNasdaq(),
                inquireSp500(),
                inquireUsdKrw()
        );
        log.info("[MARKET_KIS] 시장 지표 종합 조회 완료");

        return new MarketResponse(
                OffsetDateTime.now(),
                markets
        );
    }

    public MarketResponse.MarketItem inquireKospi(){
        log.info("[MARKET_KIS] KOSPI 지수 조회 시작");
        String token = tokenService.getValidAccessToken();

        KisDomesticIndexPriceResponse res = kisRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(props.domestic().inquireIndexPricePath())
                        .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                        .queryParam("FID_INPUT_ISCD", marketProperties.domestic().kospiCode())
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("appkey", props.appkey())
                .header("appsecret", props.appsecret())
                .header("tr_id", props.domestic().trIdInquireIndexPrice())
                .header("custtype", props.custtype())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(KisDomesticIndexPriceResponse.class);

        if (res == null) {
            log.error("[MARKET_KIS] KOSPI 조회 실패: 응답 바디가 비어있음");
            throw new IllegalArgumentException("KIS inquireKospi: empty response");
        }

        if (!"0".equals(res.rtCd())) {
            log.error("[MARKET_KIS] KOSPI 조회 API 에러: rtCd={}, msgCd={}, msg='{}'", res.rtCd(), res.msgCd(), res.msg1());
            throw new IllegalStateException("KIS inquireKospi failed: " + res.msgCd() + " / " + res.msg1());
        }

        log.info("[MARKET_KIS] KOSPI 조회 성공: 현재가={}, 등락={}", res.output().currentPrice(), res.output().change());

        KisDomesticIndexPriceResponse.Output output = res.output();

        return new MarketResponse.MarketItem(
                KOSPI_NAME,
                toBigDecimal(output.currentPrice()),
                toBigDecimal(output.change()),
                toBigDecimal(output.changeRate()),
                DirectionMapper.fromKisSign(output.sign())
        );

    }

    public MarketResponse.MarketItem inquireNasdaq() {
        return inquireOverseasMarket(
                NASDAQ_NAME,
                "N",
                marketProperties.overseas().nasdaqCode()
        );
    }

    public MarketResponse.MarketItem inquireSp500() {
        return inquireOverseasMarket(
                SP500_NAME,
                "N",
                marketProperties.overseas().sp500Code()
        );
    }

    public MarketResponse.MarketItem inquireUsdKrw() {
        return inquireOverseasMarket(
                USD_KRW_NAME,
                "X",
                marketProperties.overseas().usdKrwCode()
        );
    }

    private MarketResponse.MarketItem inquireOverseasMarket(
            String displayName,
            String marketDivCode,
            String inputIscd
    ) {
        log.info("[MARKET_KIS] 해외 시장 지표 조회 시작: name={}, code={}", displayName, inputIscd);
        String token = tokenService.getValidAccessToken();

        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(7);

        KisOverseasChartPriceResponse res = kisRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(props.overseas().inquireDailyChartPricePath())
                        .queryParam("FID_COND_MRKT_DIV_CODE", marketDivCode)
                        .queryParam("FID_INPUT_ISCD", inputIscd)
                        .queryParam("FID_INPUT_DATE_1", from.format(DateTimeFormatter.BASIC_ISO_DATE))
                        .queryParam("FID_INPUT_DATE_2", today.format(DateTimeFormatter.BASIC_ISO_DATE))
                        .queryParam("FID_PERIOD_DIV_CODE", "D")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("appkey", props.appkey())
                .header("appsecret", props.appsecret())
                .header("tr_id", props.overseas().trIdInquireDailyChartPrice())
                .header("custtype", props.custtype())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(KisOverseasChartPriceResponse.class);

        if (res == null) {
            log.error("[MARKET_KIS] 해외시장({}) 조회 실패: 응답 바디가 비어있음", displayName);
            throw new IllegalStateException("KIS inquireOverseasMarket(" + displayName + "): empty response");
        }
        if (!"0".equals(res.rtCd())) {
            log.error("[MARKET_KIS] 해외시장({}) 조회 실패: msgCd={}, msg1={}", displayName, res.msgCd(), res.msg1());
            throw new IllegalStateException(
                    "KIS inquireOverseasMarket(" + displayName + ") failed: " + res.msgCd() + " / " + res.msg1()
            );
        }


        log.info("[MARKET_KIS] 해외 시장({}) 조회 성공: 현재가={}", displayName, res.output1().currentPrice());
        KisOverseasChartPriceResponse.Output1 output1 = res.output1();

        return new MarketResponse.MarketItem(
                displayName,
                toBigDecimal(output1.currentPrice()),
                toBigDecimal(output1.change()),
                toBigDecimal(output1.changeRate()),
                DirectionMapper.fromKisSign(output1.sign())
        );
    }

    private BigDecimal toBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.trim()).abs();
    }
}
