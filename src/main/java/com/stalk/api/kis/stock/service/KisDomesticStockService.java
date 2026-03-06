package com.stalk.api.kis.stock.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stalk.api.kis.KisProperties;
import com.stalk.api.kis.KisTokenService;
import com.stalk.api.kis.stock.dto.KisInquirePriceResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
public class KisDomesticStockService {

    private final RestClient kisRestClient;
    private final KisProperties props;
    private final KisTokenService tokenService;

    public KisDomesticStockService(RestClient kisRestClient, KisProperties props, KisTokenService tokenService) {
        this.kisRestClient = kisRestClient;
        this.props = props;
        this.tokenService = tokenService;
    }

    public KisInquirePriceResponse inquirePrice(String code, String marketDiv) {
        String token = tokenService.getValidAccessToken();

        KisInquirePriceResponse res = kisRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(props.domestic().inquirePricePath())
                        .queryParam("FID_COND_MRKT_DIV_CODE", marketDiv) // J:KRX, NX:NXT, UN:통합
                        .queryParam("FID_INPUT_ISCD", code) // 종목코드 (ex 005930 삼성전자) // ETN은 종목코드 6자리 앞에 Q 입력 필수
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")  // application/json; charset=utf-8
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)  // OAuth 토큰이 필요한 API 경우 발급한 Access token // 일반고객(Access token 유효기간 1일, OAuth 2.0의 Client Credentials Grant 절차를 준용)
                .header("appkey", props.appkey())
                .header("appsecret", props.appsecret())
                .header("tr_id", props.domestic().trIdInquirePrice())
                .header("custtype", props.custtype())  // B : 법인 / P : 개인
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(KisInquirePriceResponse.class);

        // rt_cd == "0" 이 성공인 패턴(문서 응답 바디에 rt_cd/msg_cd/msg1 존재)
        if (res == null) throw new IllegalStateException("KIS inquirePrice: empty response");
        if (!"0".equals(res.rtCd())) {
            throw new IllegalStateException("KIS inquirePrice failed: " + res.msgCd() + " / " + res.msg1());
        }
        return res;
    }
}
