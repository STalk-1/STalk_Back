package com.stalk.api.kis.market.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "국내 업종 현재지수 응답")
public record KisDomesticIndexPriceResponse(
        @Schema(description = "성공 실패 여부", example = "0")
        @JsonProperty("rt_cd")
        String rtCd,

        @Schema(description = "응답 코드")
        @JsonProperty("msg_cd")
        String msgCd,

        @Schema(description = "응답 메시지")
        @JsonProperty("msg1")
        String msg1,

        @Schema(description = "응답 상세")
        @JsonProperty("output")
        Output output
) {
    public record Output(
            @Schema(description = "업종 지수 현재가", example = "2550.72")
            @JsonProperty("bstp_nmix_prpr")
            String currentPrice,

            @Schema(description = "전일 대비", example = "-15.26")
            @JsonProperty("bstp_nmix_prdy_vrss")
            String change,

            @Schema(description = "전일 대비 부호", example = "5")
            @JsonProperty("prdy_vrss_sign")
            String sign,

            @Schema(description = "전일 대비율", example = "-0.59")
            @JsonProperty("bstp_nmix_prdy_ctrt")
            String changeRate
    ) {}
}