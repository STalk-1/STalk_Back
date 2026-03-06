package com.stalk.api.kis.market.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "해외 지수/환율 기간별 시세 응답")
public record KisOverseasChartPriceResponse(
        @Schema(description = "성공 실패 여부", example = "0")
        @JsonProperty("rt_cd")
        String rtCd,

        @Schema(description = "응답 코드")
        @JsonProperty("msg_cd")
        String msgCd,

        @Schema(description = "응답 메시지")
        @JsonProperty("msg1")
        String msg1,

        @Schema(description = "응답 상세1")
        @JsonProperty("output1")
        Output1 output1
) {
    public record Output1(
            @Schema(description = "전일 대비", example = "-52.14")
            @JsonProperty("ovrs_nmix_prdy_vrss")
            String change,

            @Schema(description = "전일 대비 부호", example = "5")
            @JsonProperty("prdy_vrss_sign")
            String sign,

            @Schema(description = "전일 대비율", example = "-0.30")
            @JsonProperty("prdy_ctrt")
            String changeRate,

            @Schema(description = "현재가", example = "17550.01")
            @JsonProperty("ovrs_nmix_prpr")
            String currentPrice,

            @Schema(description = "한글 종목명")
            @JsonProperty("hts_kor_isnm")
            String name
    ) {}
}