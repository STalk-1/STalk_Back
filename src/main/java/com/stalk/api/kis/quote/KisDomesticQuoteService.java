package com.stalk.api.kis.quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stalk.api.kis.KisProperties;
import com.stalk.api.kis.KisTokenService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
public class KisDomesticQuoteService {

    private final RestClient kisRestClient;
    private final KisProperties props;
    private final KisTokenService tokenService;

    public KisDomesticQuoteService(RestClient kisRestClient, KisProperties props, KisTokenService tokenService) {
        this.kisRestClient = kisRestClient;
        this.props = props;
        this.tokenService = tokenService;
    }


    public InquirePriceResponse inquirePrice(String code, String marketDiv) {
        String token = tokenService.getValidAccessToken();

        InquirePriceResponse res = kisRestClient.get()
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
                .body(InquirePriceResponse.class);

        // rt_cd == "0" 이 성공인 패턴(문서 응답 바디에 rt_cd/msg_cd/msg1 존재)
        if (res == null) throw new IllegalStateException("KIS inquirePrice: empty response");
        if (!"0".equals(res.rtCd())) {
            throw new IllegalStateException("KIS inquirePrice failed: " + res.msgCd() + " / " + res.msg1());
        }
        return res;
    }
    @Schema(description = "국내 주식 현재가 시세 응답")
    public record InquirePriceResponse(
            @Schema(description = "성공 실패 여부 (0: 성공, 이외: 실패)", example = "0")
            @JsonProperty("rt_cd") String rtCd,     // 성공 실패 여부

            @Schema(description = "응답 코드", example = "OPSP0000")
            @JsonProperty("msg_cd") String msgCd,   // 응답코드

            @Schema(description = "응답 메시지", example = "정상처리되었습니다.")
            @JsonProperty("msg1") String msg1,      // 응답메세지

            @Schema(description = "주식 현재가 상세 정보")
            @JsonProperty("output") Output output   // 응답상세
    ) {
        // 필요한 필드만 최소로 매핑 (문서 output은 매우 많음)
        @Schema(description = "주식 현재가 상세 결과")
        public record Output(
                @Schema(description = "주식 현재가", example = "75500")
                @JsonProperty("stck_prpr") String currentPrice,   // 주식 현재가

                @Schema(description = "전일 대비", example = "300")
                @JsonProperty("prdy_vrss") String diff,           // 전일 대비

                @Schema(description = "전일 대비 부호 (1: 상한, 2: 상승, 3: 보합, 4: 하한, 5: 하락)", example = "2")
                @JsonProperty("prdy_vrss_sign") String sign,      // 전일 대비 부호

                @Schema(description = "전일 대비율", example = "0.40")
                @JsonProperty("prdy_ctrt") String diffRate,       // 전일 대비율

                @Schema(description = "누적 거래량", example = "1234567")
                @JsonProperty("acml_vol") String accVolume,       // 누적 거래량

                @Schema(description = "누적 거래대금", example = "93200000000")
                @JsonProperty("acml_tr_pbmn") String accAmount,   // 누적 거래대금

                @Schema(description = "시가", example = "75200")
                @JsonProperty("stck_oprc") String openPrice,      // 시가

                @Schema(description = "고가", example = "76000")
                @JsonProperty("stck_hgpr") String highPrice,      // 고가

                @Schema(description = "저가", example = "75100")
                @JsonProperty("stck_lwpr") String lowPrice        // 저가
        ) {}
    }
}
